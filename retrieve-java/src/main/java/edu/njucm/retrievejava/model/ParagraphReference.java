package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table
public class ParagraphReference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "relation_id")
    private Long relationId;

    @ManyToOne
    @JoinColumn(name = "paragraph_id")
    private Paragraph paragraph;

    @ManyToOne
    @JoinColumn(name = "ref_id")
    private Reference reference;

    private String text;
    private int start;
    private int end;


    @Override
    public String toString() {
        return "ParagraphReference{" +
                "relationId=" + relationId +
                ", paragraph=" + paragraph +
                ", reference=" + reference +
                ", text='" + text + '\'' +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public Long getRelationId() {
        return relationId;
    }

    public void setRelationId(Long relationId) {
        this.relationId = relationId;
    }

    public Paragraph getParagraph() {
        return paragraph;
    }

    public void setParagraph(Paragraph paragraph) {
        this.paragraph = paragraph;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public ParagraphReference() {
    }

    public ParagraphReference(Long relationId, Paragraph paragraph, Reference reference, String text, int start, int end) {
        this.relationId = relationId;
        this.paragraph = paragraph;
        this.reference = reference;
        this.text = text;
        this.start = start;
        this.end = end;
    }
}
