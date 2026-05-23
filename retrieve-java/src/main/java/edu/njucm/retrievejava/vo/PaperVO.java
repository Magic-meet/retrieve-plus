package edu.njucm.retrievejava.vo;

public class PaperVO {
    private Long paperId;
    private String title;
    private String year;
    private String venue;
    private String keywords;
    private String abstractContent;
    private String authors;

    public PaperVO(Long paperId, String title, String year, String venue, String keywords, String abstractContent, String authors) {
        this.paperId = paperId;
        this.title = title;
        this.year = year;
        this.venue = venue;
        this.keywords = keywords;
        this.abstractContent = abstractContent;
        this.authors = authors;
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

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public PaperVO() {
    }

    public Long getPaperId() {
        return paperId;
    }

    public void setPaperId(Long paperId) {
        this.paperId = paperId;
    }
}
