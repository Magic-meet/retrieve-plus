package edu.njucm.retrievejava.service;

import edu.njucm.retrievejava.vo.ChunkVO;
import edu.njucm.retrievejava.vo.DocumentVO;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    DocumentVO uploadDocument(MultipartFile file) throws IOException;

    List<DocumentVO> getDocuments();

    DocumentVO getDocument(Long documentId) throws IOException;

    List<ChunkVO> getDocumentChunks(Long documentId) throws IOException;

    ResponseEntity<ByteArrayResource> downloadDocumentSource(Long documentId) throws IOException;

    void deleteDocument(Long documentId);
}
