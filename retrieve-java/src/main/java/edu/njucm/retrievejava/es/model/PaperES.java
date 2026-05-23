package edu.njucm.retrievejava.es.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "paper", createIndex = true)
public class PaperES {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long paperId;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text)
    private String year;
    @Field(type = FieldType.Text)
    private String venue;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String keywords;
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String abstractContent;
    @Field(type = FieldType.Dense_Vector,dims = 1024)
    private Double[] embedding;

    @Override
    public String toString() {
        return "Paper{" +
                "id='" + id + '\'' +
                ", paperId=" + paperId +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", venue='" + venue + '\'' +
                ", keywords='" + keywords + '\'' +
                ", abstractContent='" + abstractContent + '\'' +
                ", embedding=" + Arrays.toString(embedding) +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAbstractContent() {
        return abstractContent;
    }

    public void setAbstractContent(String abstractContent) {
        this.abstractContent = abstractContent;
    }

    public Double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(Double[] embedding) {
        this.embedding = embedding;
    }

    public PaperES() {
    }

    public PaperES(String id, Long paperId, String title, String year, String venue, String keywords, String abstractContent, Double[] embedding) {
        this.id = id;
        this.paperId = paperId;
        this.title = title;
        this.year = year;
        this.venue = venue;
        this.keywords = keywords;
        this.abstractContent = abstractContent;
        this.embedding = embedding;
    }
}
