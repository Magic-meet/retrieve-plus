package edu.njucm.retrievejava.service;

import org.csource.common.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String[] uploadFileToDFS(MultipartFile multipartFile) throws MyException, IOException;

    byte[] downloadFileFromDFS(String storageGroup, String storagePath) throws MyException, IOException;

    int deleteFileFromDFS(String storageGroup, String storagePath) throws MyException, IOException;
}
