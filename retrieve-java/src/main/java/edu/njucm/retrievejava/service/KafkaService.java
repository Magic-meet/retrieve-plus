package edu.njucm.retrievejava.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import edu.njucm.retrievejava.model.Paper;
import org.springframework.web.multipart.MultipartFile;

public interface KafkaService {
    void SendPDFFile(MultipartFile file, Long paperInfoId);

    Paper RecieveJsonAndSave(String message) throws JsonProcessingException;
}
