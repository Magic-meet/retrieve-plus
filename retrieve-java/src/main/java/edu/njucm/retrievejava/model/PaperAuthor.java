package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table
public class PaperAuthor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long relationId;

    @ManyToOne
    @JoinColumn(name = "paper_id")
    private Paper paper;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    private int authorRank;

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

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public int getAuthorRank() {
        return authorRank;
    }

    public void setAuthorRank(int authorRank) {
        this.authorRank = authorRank;
    }

    public PaperAuthor() {
    }

    public PaperAuthor(Long relationId, Paper paper, Author author, int authorRank) {
        this.relationId = relationId;
        this.paper = paper;
        this.author = author;
        this.authorRank = authorRank;
    }
}
