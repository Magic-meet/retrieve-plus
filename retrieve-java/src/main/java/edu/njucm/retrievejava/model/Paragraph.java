package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
public class Paragraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "paragraph_id")
    private Long paragraphId;

    private String type;

    private String section;

    private String secNum;

    @Column(length = 4000)
    private String text;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @OneToMany(mappedBy = "paragraph",fetch = FetchType.EAGER)
    private Set<ParagraphReference> references = new HashSet<>();


    @Override
    public String toString() {
        return "Paragraph{" +
                "paragraphId=" + paragraphId +
                ", type='" + type + '\'' +
                ", section='" + section + '\'' +
                ", secNum='" + secNum + '\'' +
                ", text='" + text + '\'' +
                ", paper=" + paper +
                ", references=" + references +
                '}';
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

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Set<ParagraphReference> getReferences() {
        return references;
    }

    public void setReferences(Set<ParagraphReference> references) {
        this.references = references;
    }

    public Paragraph() {
    }

    public Paragraph(Long paragraphId, String type, String section, String secNum, String text, Paper paper, Set<ParagraphReference> references) {
        this.paragraphId = paragraphId;
        this.type = type;
        this.section = section;
        this.secNum = secNum;
        this.text = text;
        this.paper = paper;
        this.references = references;
    }
    public Double getSecNumAsDouble() {
        try {
            return Double.parseDouble(secNum);
        } catch (NumberFormatException e) {
            // 如果无法解析为浮点数，返回一个默认值或者抛出异常，具体根据你的需求而定
            return 0.0; // 这里返回默认值0.0
        }
    }
}
