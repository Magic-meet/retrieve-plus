package edu.njucm.retrievejava.es.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "paragraph",createIndex = true)
public class ParagraphES {
    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long paragraphId;

    @Field(type = FieldType.Text)
    private String type;
    @Field(type = FieldType.Text)
    private String section;
    @Field(type = FieldType.Text)
    private String secNum;

    @Field(type = FieldType.Text ,analyzer = "ik_max_word")
    private String text;
    @Field(type = FieldType.Long)
    private Long paperId;

    @Override
    public String toString() {
        return "Paragraph{" +
                "id='" + id + '\'' +
                ", paragraphId=" + paragraphId +
                ", type='" + type + '\'' +
                ", section='" + section + '\'' +
                ", secNum='" + secNum + '\'' +
                ", text='" + text + '\'' +
                ", paperId=" + paperId +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getParagraphId() {
        return paragraphId;
    }

    public void setParagraphId(Long paragraphId) {
        this.paragraphId = paragraphId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSecNum() {
        return secNum;
    }

    public void setSecNum(String secNum) {
        this.secNum = secNum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }

    public ParagraphES() {
    }

    public ParagraphES(String id, Long paragraphId, String type, String section, String secNum, String text, Long paperId) {
        this.id = id;
        this.paragraphId = paragraphId;
        this.type = type;
        this.section = section;
        this.secNum = secNum;
        this.text = text;
        this.paperId = paperId;
    }

}
