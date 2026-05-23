package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table
public class PaperReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @ManyToOne
    @JoinColumn(name = "ref_id")
    private Reference reference;

    private String refNum;
    @Column(length = 4000)
    private String rawText;

    @Override
    public String toString() {
        return "PaperReference{" +
                "relationId=" + relationId +
                ", paper=" + paper +
                ", reference=" + reference +
                ", refNum='" + refNum + '\'' +
                ", rawText='" + rawText + '\'' +
                '}';
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public String getRefNum() {
        return refNum;
    }

    public void setRefNum(String refNum) {
        this.refNum = refNum;
    }

    public String getRawText() {
        return rawText;
    }

    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    public PaperReference() {
    }

    public PaperReference(Long relationId, Paper paper, Reference reference, String refNum, String rawText) {
        this.relationId = relationId;
        this.paper = paper;
        this.reference = reference;
        this.refNum = refNum;
        this.rawText = rawText;
    }
}
