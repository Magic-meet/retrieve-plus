package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table
public class PaperInfo {
    private static final int MESSAGE_MAX_LENGTH = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    private String originalFileName;

    private Long fileSize;

    private LocalDateTime uploadTime;

    private String uploadStatus;

    private String parseStatus;

    private String storageGroup;

    private String storagePath;

    @Column(length = MESSAGE_MAX_LENGTH)
    private String message;

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(String parseStatus) {
        this.parseStatus = parseStatus;
    }

    public String getStorageGroup() {
        return storageGroup;
    }

    public void setStorageGroup(String storageGroup) {
        this.storageGroup = storageGroup;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message == null) {
            this.message = null;
            return;
        }
        if (message.length() <= MESSAGE_MAX_LENGTH) {
            this.message = message;
            return;
        }
        this.message = message.substring(0, MESSAGE_MAX_LENGTH - 3) + "...";
    }
}
