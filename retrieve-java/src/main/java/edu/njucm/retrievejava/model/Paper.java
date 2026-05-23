package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paperId;
    private String title;
    private String year;
    private String venue;
    private String keywords;
    @Column(length = 4000)
    private String abstractContent;

    @OneToMany(mappedBy = "paper",fetch = FetchType.EAGER)
    private Set<Paragraph> paragraphs = new HashSet<>();

    @OneToMany(mappedBy = "paper",fetch = FetchType.EAGER)
    private Set<PaperAuthor> authors = new HashSet<>();

    @OneToMany(mappedBy = "paper",fetch = FetchType.EAGER)
    private Set<PaperReference> references = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "paper_info", referencedColumnName = "relationId")
    private PaperInfo paperInfo;



    @Override
    public String toString() {
        return "Paper{" +
                "paperId=" + paperId +
                ", title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", venue='" + venue + '\'' +
                ", keywords='" + keywords + '\'' +
                ", abstractContent='" + abstractContent + '\'' +
                ", paragraphs=" + paragraphs +
                ", authors=" + authors +
                ", references=" + references +
                ", paperInfo=" + paperInfo +
                '}';
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

    public Set<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(Set<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }

    public Set<PaperAuthor> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<PaperAuthor> authors) {
        this.authors = authors;
    }

    public Set<PaperReference> getReferences() {
        return references;
    }

    public void setReferences(Set<PaperReference> references) {
        this.references = references;
    }

    public PaperInfo getPaperInfo() {
        return paperInfo;
    }

    public void setPaperInfo(PaperInfo paperInfo) {
        this.paperInfo = paperInfo;
    }

    public Paper(Long paperId, String title, String year, String venue, String keywords, String abstractContent, Set<Paragraph> paragraphs, Set<PaperAuthor> authors, Set<PaperReference> references, PaperInfo paperInfo) {
        this.paperId = paperId;
        this.title = title;
        this.year = year;
        this.venue = venue;
        this.keywords = keywords;
        this.abstractContent = abstractContent;
        this.paragraphs = paragraphs;
        this.authors = authors;
        this.references = references;
        this.paperInfo = paperInfo;
    }

    public Paper() {
    }
}
