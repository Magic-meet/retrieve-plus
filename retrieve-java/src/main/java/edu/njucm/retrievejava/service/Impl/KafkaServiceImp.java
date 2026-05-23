package edu.njucm.retrievejava.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.njucm.retrievejava.dao.*;
import edu.njucm.retrievejava.model.*;
import edu.njucm.retrievejava.service.KafkaService;
import edu.njucm.retrievejava.untils.FileTools;
import edu.njucm.retrievejava.untils.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static edu.njucm.retrievejava.untils.JsonParser.extractAuthors;
import static edu.njucm.retrievejava.untils.JsonParser.extractPaper;

@Service
public class KafkaServiceImp implements KafkaService {
    private static final Logger log = LoggerFactory.getLogger(KafkaServiceImp.class);

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private PaperAuthorRepository paperAuthorRepository;

    @Autowired
    private PaperReferenceRepository paperReferenceRepository;

    @Autowired
    private ParagraphReferenceRepository paragraphReferenceRepository;

    @Autowired
    private PaperInfoRepository paperInfoRepository;

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    private final String sendPDFTopic = "pdf";

    private final String recieveJSONTopic = "json";




    @Override
    public void SendPDFFile(MultipartFile multipartFile, Long paperInfoId) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            // TODO : 判断文件后缀是否正确
            if (fileName != null) {
                String fileExtName = FileTools.getFileExtName(fileName);
            }
            byte[] fileBytes = multipartFile.getBytes();
            String kafkaKey = paperInfoId + ":" + fileName;
            int chunkSize = 1024 * 512; // 每个文件块的大小（512 KB）
            int offset = 0;
            while (offset < fileBytes.length) {
                int length = Math.min(chunkSize, fileBytes.length - offset);
                byte[] chunk = new byte[length];
                System.arraycopy(fileBytes, offset, chunk, 0, length);
                // 使用文件名作为键发送消息
                kafkaTemplate.send(sendPDFTopic, kafkaKey, chunk);
                offset += length;
            }
            kafkaTemplate.send(sendPDFTopic, kafkaKey, "EOF".getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @KafkaListener(topics = "json")
    public Paper RecieveJsonAndSave(String message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(message);
        Long paperInfoId = null;
        if (root.has("_upload_record_id") && !root.get("_upload_record_id").isNull()) {
            paperInfoId = root.get("_upload_record_id").asLong();
        }

        final String sourceFileName = root.has("_source_file_name") && !root.get("_source_file_name").isNull()
                ? root.get("_source_file_name").asText() : "unknown";
        final String parsedTitle = root.has("title") && !root.get("title").isNull()
                ? root.get("title").asText() : "unknown";
        final Long uploadRecordId = paperInfoId;

        log.info("Received parsed paper message, uploadRecordId={}, sourceFileName={}, title={}",
                paperInfoId, sourceFileName, parsedTitle);

        try {
            if (uploadRecordId != null) {
                Paper existingPaper = paperRepository.findByPaperInfoRelationId(uploadRecordId);
                if (existingPaper != null) {
                    log.info("Skip duplicate parsed paper message because paper already exists, uploadRecordId={}, paperId={}",
                            uploadRecordId, existingPaper.getPaperId());
                    paperInfoRepository.findById(uploadRecordId).ifPresent(paperInfo -> {
                        paperInfo.setParseStatus("PARSED");
                        paperInfo.setMessage("解析完成，重复消息已忽略");
                        paperInfoRepository.save(paperInfo);
                    });
                    return existingPaper;
                }
            }

            List<Author> authors = extractAuthors(root);
            Paper paper = extractPaper(root);
            log.info("Extracted paper payload, title={}, authorsCount={}", paper.getTitle(), authors.size());

            List<PaperAuthor> paperAuthors = new ArrayList<>();
            for (int i=0;i<authors.size();){
                paperAuthors.add(new PaperAuthor(null,paper,authors.get(i),++i));
            }

            Map<String, Object> objectMap = JsonParser.extractParagraphs(root);
            List<Paragraph> paragraphs = (List<Paragraph>) objectMap.getOrDefault("paragraphs", Collections.emptyList());
            for (Paragraph paragraph : paragraphs){
                paragraph.setPaper(paper);
            }
            List<ParagraphReference> paragraphReferences = (List<ParagraphReference>) objectMap.getOrDefault("paragraphReferences", Collections.emptyList());

            objectMap = JsonParser.PaperReferences(root);
            List<PaperReference> paperReferences = (List<PaperReference>) objectMap.getOrDefault("paperReferences", Collections.emptyList());
            for (PaperReference paperReference : paperReferences){
                paperReference.setPaper(paper);
            }
            List<Reference> references = (List<Reference>) objectMap.getOrDefault("references", Collections.emptyList());
            JsonParser.updateParagraphReferences(paperReferences,paragraphReferences);

            log.info("Prepared entities for persistence, paragraphsCount={}, paragraphReferencesCount={}, paperReferencesCount={}, referencesCount={}",
                    paragraphs.size(), paragraphReferences.size(), paperReferences.size(), references.size());

            if (paperInfoId != null) {
                Optional<PaperInfo> paperInfoOptional = paperInfoRepository.findById(paperInfoId);
                if (paperInfoOptional.isPresent()) {
                    paper.setPaperInfo(paperInfoOptional.get());
                    log.info("Linked paper to paper_info, uploadRecordId={}", paperInfoId);
                } else {
                    log.warn("paper_info not found for uploadRecordId={}", paperInfoId);
                }
            } else {
                log.warn("Parsed message missing _upload_record_id, sourceFileName={}", sourceFileName);
            }

            paperRepository.save(paper);
            log.info("Saved paper entity, paperId={}, title={}, paperInfoId={}",
                    paper.getPaperId(), paper.getTitle(),
                    paper.getPaperInfo() != null ? paper.getPaperInfo().getRelationId() : null);

            authorRepository.saveAll(authors);
            paragraphRepository.saveAll(paragraphs);
            referenceRepository.saveAll(references);
            paperAuthorRepository.saveAll(paperAuthors);
            paperReferenceRepository.saveAll(paperReferences);
            paragraphReferenceRepository.saveAll(paragraphReferences);
            log.info("Saved related entities for paperId={}", paper.getPaperId());

            if (uploadRecordId != null) {
                paperInfoRepository.findById(uploadRecordId).ifPresentOrElse(paperInfo -> {
                    paperInfo.setParseStatus("PARSED");
                    paperInfo.setMessage("解析完成，文献信息已写入系统");
                    paperInfoRepository.save(paperInfo);
                    log.info("Updated paper_info parse status to PARSED, uploadRecordId={}", uploadRecordId);
                }, () -> log.warn("Unable to update parse status because paper_info was not found, uploadRecordId={}", uploadRecordId));
            }

            return paper;
        } catch (Exception e) {
            log.error("Failed to persist parsed paper message, uploadRecordId={}, sourceFileName={}, title={}",
                    paperInfoId, sourceFileName, parsedTitle, e);
            if (uploadRecordId != null) {
                final String errorMessage = e.getMessage();
                paperInfoRepository.findById(uploadRecordId).ifPresent(paperInfo -> {
                    paperInfo.setParseStatus("FAILED");
                    paperInfo.setMessage("解析结果写入失败: " + errorMessage);
                    paperInfoRepository.save(paperInfo);
                    log.info("Updated paper_info parse status to FAILED, uploadRecordId={}", uploadRecordId);
                });
            }
            throw e;
        }
    }


}
