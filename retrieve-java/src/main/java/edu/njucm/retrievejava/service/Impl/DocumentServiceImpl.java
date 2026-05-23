package edu.njucm.retrievejava.service.Impl;

import edu.njucm.retrievejava.dao.*;
import edu.njucm.retrievejava.es.dao.PaperElasticsearchRepository;
import edu.njucm.retrievejava.es.dao.ParagraphElasticsearchRepository;
import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperInfo;
import edu.njucm.retrievejava.model.Paragraph;
import edu.njucm.retrievejava.model.PaperAuthor;
import edu.njucm.retrievejava.service.DocumentService;
import edu.njucm.retrievejava.service.FileService;
import edu.njucm.retrievejava.service.KafkaService;
import edu.njucm.retrievejava.vo.ChunkVO;
import edu.njucm.retrievejava.vo.DocumentVO;
import org.csource.common.MyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private PaperRepository paperRepository;
    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private PaperInfoRepository paperInfoRepository;
    @Autowired
    private ParagraphReferenceRepository paragraphReferenceRepository;
    @Autowired
    private PaperAuthorRepository paperAuthorRepository;
    @Autowired
    private PaperReferenceRepository paperReferenceRepository;
    @Autowired
    private PaperElasticsearchRepository paperElasticsearchRepository;
    @Autowired
    private ParagraphElasticsearchRepository paragraphElasticsearchRepository;
    @Autowired
    private FileService fileService;
    @Autowired
    private KafkaService kafkaService;

    @Override
    public DocumentVO uploadDocument(MultipartFile file) throws IOException {
        PaperInfo paperInfo = new PaperInfo();
        paperInfo.setOriginalFileName(file.getOriginalFilename());
        paperInfo.setFileSize(file.getSize());
        paperInfo.setUploadTime(LocalDateTime.now());
        paperInfo.setUploadStatus("UPLOADING");
        paperInfo.setParseStatus("WAITING");
        paperInfo.setMessage("文件正在上传到 FastDFS");
        paperInfo = paperInfoRepository.save(paperInfo);

        try {
            String[] uploadResults = fileService.uploadFileToDFS(file);
            paperInfo.setStorageGroup(uploadResults[0]);
            paperInfo.setStoragePath(uploadResults[1]);
            paperInfo.setUploadStatus("STORED");
            paperInfo.setParseStatus("PARSING");
            paperInfo.setMessage("源文件已保存，正在解析文献内容");
            paperInfo = paperInfoRepository.save(paperInfo);
            kafkaService.SendPDFFile(file, paperInfo.getRelationId());
            return toDocumentVO(paperInfo, paperRepository.findByPaperInfoRelationId(paperInfo.getRelationId()));
        } catch (Exception e) {
            paperInfo.setUploadStatus("FAILED");
            paperInfo.setParseStatus("NOT_STARTED");
            paperInfo.setMessage(e.getMessage());
            paperInfoRepository.save(paperInfo);
            throw new IOException("上传文件失败：" + e.getMessage(), e);
        }
    }

    @Override
    public List<DocumentVO> getDocuments() {
        List<DocumentVO> documents = new ArrayList<>();
        for (PaperInfo paperInfo : paperInfoRepository.findAllByOrderByUploadTimeDesc()) {
            documents.add(toDocumentVO(paperInfo, paperRepository.findByPaperInfoRelationId(paperInfo.getRelationId())));
        }
        return documents;
    }

    @Override
    public DocumentVO getDocument(Long documentId) throws IOException {
        PaperInfo paperInfo = getPaperInfo(documentId);
        return toDocumentVO(paperInfo, paperRepository.findByPaperInfoRelationId(documentId));
    }

    @Override
    public List<ChunkVO> getDocumentChunks(Long documentId) throws IOException {
        Paper paper = getPaperByDocumentId(documentId);
        List<Paragraph> paragraphList = paragraphRepository.findAllByPaper(paper);
        return paragraphList.stream()
                .sorted(Comparator.comparing(paragraph -> paragraph.getParagraphId() == null ? Long.MAX_VALUE : paragraph.getParagraphId()))
                .map(this::toChunkVO)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<ByteArrayResource> downloadDocumentSource(Long documentId) throws IOException {
        PaperInfo paperInfo = getPaperInfo(documentId);
        if (paperInfo.getStorageGroup() == null || paperInfo.getStoragePath() == null) {
            throw new IOException("该文献还没有可下载的源文件");
        }
        try {
            byte[] fileBytes = fileService.downloadFileFromDFS(paperInfo.getStorageGroup(), paperInfo.getStoragePath());
            ByteArrayResource resource = new ByteArrayResource(fileBytes);
            String fileName = paperInfo.getOriginalFileName() == null ? "paper.pdf" : paperInfo.getOriginalFileName();
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(fileBytes.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(fileName).build().toString())
                    .body(resource);
        } catch (MyException e) {
            throw new IOException("获取源文件失败：" + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {
        PaperInfo paperInfo = paperInfoRepository.findById(documentId).orElse(null);
        if (paperInfo == null) {
            return;
        }

        if (paperInfo.getStorageGroup() != null && paperInfo.getStoragePath() != null) {
            try {
                fileService.deleteFileFromDFS(paperInfo.getStorageGroup(), paperInfo.getStoragePath());
            } catch (Exception ignored) {
            }
        }

        Paper paper = paperRepository.findByPaperInfoRelationId(documentId);
        if (paper != null) {
            List<Paragraph> paragraphs = paragraphRepository.findAllByPaper(paper);
            for (Paragraph paragraph : paragraphs) {
                paragraphReferenceRepository.deleteAllByParagraph(paragraph);
                if (paragraph.getParagraphId() != null) {
                    paragraphElasticsearchRepository.deleteByParagraphId(paragraph.getParagraphId());
                }
            }
            paperReferenceRepository.deleteAllByPaper(paper);
            paperAuthorRepository.deleteAllByPaper(paper);
            paragraphRepository.deleteAllByPaper(paper);
            if (paper.getPaperId() != null) {
                paperElasticsearchRepository.deleteByPaperId(paper.getPaperId());
            }
            paperRepository.delete(paper);
        }

        paperInfoRepository.delete(paperInfo);
    }

    private DocumentVO toDocumentVO(PaperInfo paperInfo, Paper paper) {
        DocumentVO documentVO = new DocumentVO();
        documentVO.setDocumentId(paperInfo.getRelationId());
        documentVO.setOriginalFileName(paperInfo.getOriginalFileName());
        documentVO.setFileSize(paperInfo.getFileSize());
        documentVO.setUploadTime(paperInfo.getUploadTime());
        documentVO.setUploadStatus(paperInfo.getUploadStatus());
        documentVO.setParseStatus(paperInfo.getParseStatus());
        documentVO.setStorageGroup(paperInfo.getStorageGroup());
        documentVO.setStoragePath(paperInfo.getStoragePath());
        documentVO.setMessage(paperInfo.getMessage());
        if (paper != null) {
            documentVO.setPaperId(paper.getPaperId());
            documentVO.setTitle(paper.getTitle());
            documentVO.setYear(paper.getYear());
            documentVO.setVenue(paper.getVenue());
            documentVO.setAbstractContent(paper.getAbstractContent());
            documentVO.setAuthors(joinAuthors(paper));
            documentVO.setChunkCount(paragraphRepository.findAllByPaper(paper).size());
        }
        return documentVO;
    }

    private ChunkVO toChunkVO(Paragraph paragraph) {
        ChunkVO chunkVO = new ChunkVO();
        chunkVO.setChunkId(paragraph.getParagraphId());
        chunkVO.setSection(paragraph.getSection());
        chunkVO.setSecNum(paragraph.getSecNum());
        chunkVO.setType(paragraph.getType());
        chunkVO.setText(paragraph.getText());
        return chunkVO;
    }

    private String joinAuthors(Paper paper) {
        return paper.getAuthors().stream()
                .sorted(Comparator.comparingInt(PaperAuthor::getAuthorRank))
                .map(paperAuthor -> paperAuthor.getAuthor().getName())
                .collect(Collectors.joining("; "));
    }

    private PaperInfo getPaperInfo(Long documentId) throws IOException {
        return paperInfoRepository.findById(documentId)
                .orElseThrow(() -> new IOException("文献记录不存在"));
    }

    private Paper getPaperByDocumentId(Long documentId) throws IOException {
        Paper paper = paperRepository.findByPaperInfoRelationId(documentId);
        if (paper == null) {
            throw new IOException("文献尚未完成解析");
        }
        return paper;
    }
}
