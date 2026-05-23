package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refId;

    private String title;

    private String authors;

    private String year;

    private String venue;

    @OneToMany(mappedBy = "reference",fetch = FetchType.EAGER)
    private Set<PaperReference> papers = new HashSet<>();

    @Override
    public String toString() {
        return "Reference{" +
                "refId=" + refId +
                ", title='" + title + '\'' +
                ", authors='" + authors + '\'' +
                ", year='" + year + '\'' +
                ", venue='" + venue + '\'' +
                ", papers=" + papers +
                '}';
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
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

    public Set<PaperReference> getPapers() {
        return papers;
    }

    public void setPapers(Set<PaperReference> papers) {
        this.papers = papers;
    }

    public Reference() {
    }

    public Reference(Long refId, String title, String authors, String year, String venue, Set<PaperReference> papers) {
        this.refId = refId;
        this.title = title;
        this.authors = authors;
        this.year = year;
        this.venue = venue;
        this.papers = papers;
    }
}
