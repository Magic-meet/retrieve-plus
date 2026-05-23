package edu.njucm.retrievejava.controller;

import edu.njucm.retrievejava.service.DocumentService;
import edu.njucm.retrievejava.service.RetrievalService;
import edu.njucm.retrievejava.vo.ChunkVO;
import edu.njucm.retrievejava.vo.DocumentVO;
import edu.njucm.retrievejava.vo.RetrievalRequestVO;
import edu.njucm.retrievejava.vo.RetrievalResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/v1")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private RetrievalService retrievalService;

    @PostMapping("/documents")
    DocumentVO uploadDocument(@RequestParam("file") MultipartFile file) throws IOException {
        return documentService.uploadDocument(file);
    }

    @GetMapping("/documents")
    List<DocumentVO> getDocuments() {
        return documentService.getDocuments();
    }

    @GetMapping("/documents/{documentId}")
    DocumentVO getDocument(@PathVariable Long documentId) throws IOException {
        return documentService.getDocument(documentId);
    }

    @GetMapping("/documents/{documentId}/chunks")
    List<ChunkVO> getDocumentChunks(@PathVariable Long documentId) throws IOException {
        return documentService.getDocumentChunks(documentId);
    }

    @GetMapping("/documents/{documentId}/source")
    ResponseEntity<ByteArrayResource> downloadDocumentSource(@PathVariable Long documentId) throws IOException {
        return documentService.downloadDocumentSource(documentId);
    }

    @DeleteMapping("/documents/{documentId}")
    void deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
    }

    @PostMapping("/retrieval/keyword")
    RetrievalResponseVO keywordSearch(@RequestBody RetrievalRequestVO request) throws IOException {
        return retrievalService.keywordSearch(request);
    }

    @PostMapping("/retrieval/semantic")
    RetrievalResponseVO semanticSearch(@RequestBody RetrievalRequestVO request) throws IOException {
        return retrievalService.semanticSearch(request);
    }

    @PostMapping("/retrieval/hybrid")
    RetrievalResponseVO hybridSearch(@RequestBody RetrievalRequestVO request) throws IOException {
        return retrievalService.hybridSearch(request);
    }
}
