package edu.njucm.retrievejava.vo;

import java.util.ArrayList;
import java.util.List;

public class RetrievalResultItemVO {
    private Long documentId;
    private Long paperId;
    private String title;
    private String authors;
    private String year;
    private String venue;
    private String abstractContent;
    private Double score;
    private String retrievalType;
    private List<ChunkVO> chunks = new ArrayList<>();

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getRetrievalType() {
        return retrievalType;
    }

    public void setRetrievalType(String retrievalType) {
        this.retrievalType = retrievalType;
    }

    public List<ChunkVO> getChunks() {
        return chunks;
    }

    public void setChunks(List<ChunkVO> chunks) {
        this.chunks = chunks;
    }
}
