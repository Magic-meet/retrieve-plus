package edu.njucm.retrievejava.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long authorId;
    private String name;
    private String institution;
    private String location;
    private String email;

    @OneToMany(mappedBy = "author")
    private Set<PaperAuthor> papers = new HashSet<>();

    @Override
    public String toString() {
        return "Author{" +
                "authorId=" + authorId +
                ", name='" + name + '\'' +
                ", institution='" + institution + '\'' +
                ", location='" + location + '\'' +
                ", email='" + email + '\'' +
                ", papers=" + papers +
                '}';
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<PaperAuthor> getPapers() {
        return papers;
    }

    public void setPapers(Set<PaperAuthor> papers) {
        this.papers = papers;
    }

    public Author() {
    }

    public Author(Long authorId, String name, String institution, String location, String email, Set<PaperAuthor> papers) {
        this.authorId = authorId;
        this.name = name;
        this.institution = institution;
        this.location = location;
        this.email = email;
        this.papers = papers;
    }
}
