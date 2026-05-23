package edu.njucm.retrievejava.daoTest;

import edu.njucm.retrievejava.dao.*;
import edu.njucm.retrievejava.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataStorage {

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private ParagraphRepository paragraphRepository;

    @Autowired
    private PaperAuthorRepository paperAuthorRepository;

    @Autowired
    private PaperReferenceRepository paperReferenceRepository;

    @Autowired
    private ParagraphReferenceRepository paragraphReferenceRepository;

    @Test
    public void Test1() {
        // Create authors
        Author author1 = new Author();
        author1.setName("John Doe");
        author1.setInstitution("University A");
        author1.setLocation("City X");
        author1.setEmail("john.doe@example.com");
        authorRepository.save(author1);

        Author author2 = new Author();
        author2.setName("Jane Smith");
        author2.setInstitution("University B");
        author2.setLocation("City Y");
        author2.setEmail("jane.smith@example.com");
        authorRepository.save(author2);

        // Create references
        Reference reference1 = new Reference();
        reference1.setTitle("Reference 1");
        reference1.setAuthors("Author X");
        reference1.setYear("2020");
        reference1.setVenue("Journal A");
        referenceRepository.save(reference1);

        Reference reference2 = new Reference();
        reference2.setTitle("Reference 2");
        reference2.setAuthors("Author Y");
        reference2.setYear("2021");
        reference2.setVenue("Journal B");
        referenceRepository.save(reference2);

        // Create papers
        Paper paper1 = new Paper();
        paper1.setTitle("Paper Title 1");
        paper1.setYear("2022");
        paper1.setVenue("Conference C");
        paper1.setKeywords("Keyword 1, Keyword 2");
        paper1.setAbstractContent("This is the abstract content for paper 1");
        paperRepository.save(paper1);

        Paper paper2 = new Paper();
        paper2.setTitle("Paper Title 2");
        paper2.setYear("2023");
        paper2.setVenue("Conference D");
        paper2.setKeywords("Keyword 3, Keyword 4");
        paper2.setAbstractContent("This is the abstract content for paper 2");
        paperRepository.save(paper2);

        // Create paper-author relationships
        PaperAuthor paperAuthor1 = new PaperAuthor();
        paperAuthor1.setPaper(paper1);
        paperAuthor1.setAuthor(author1);
        paperAuthor1.setAuthorRank(1);
        paperAuthorRepository.save(paperAuthor1);

        PaperAuthor paperAuthor2 = new PaperAuthor();
        paperAuthor2.setPaper(paper1);
        paperAuthor2.setAuthor(author2);
        paperAuthor2.setAuthorRank(2);
        paperAuthorRepository.save(paperAuthor2);

        PaperAuthor paperAuthor3 = new PaperAuthor();
        paperAuthor3.setPaper(paper2);
        paperAuthor3.setAuthor(author2);
        paperAuthor3.setAuthorRank(1);
        paperAuthorRepository.save(paperAuthor3);

        // Create paper-reference relationships
        PaperReference paperReference1 = new PaperReference();
        paperReference1.setPaper(paper1);
        paperReference1.setReference(reference1);
        paperReference1.setRefNum("1");
        paperReference1.setRawText("Raw text for reference 1 in paper 1");
        paperReferenceRepository.save(paperReference1);

        PaperReference paperReference2 = new PaperReference();
        paperReference2.setPaper(paper1);
        paperReference2.setReference(reference2);
        paperReference2.setRefNum("2");
        paperReference2.setRawText("Raw text for reference 2 in paper 1");
        paperReferenceRepository.save(paperReference2);

        PaperReference paperReference3 = new PaperReference();
        paperReference3.setPaper(paper2);
        paperReference3.setReference(reference2);
        paperReference3.setRefNum("1");
        paperReference3.setRawText("Raw text for reference 2 in paper 2");
        paperReferenceRepository.save(paperReference3);

        // Create paragraphs
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setType("Type A");
        paragraph1.setSection("Section 1");
        paragraph1.setSecNum("1");
        paragraph1.setText("Paragraph text 1");
        paragraph1.setPaper(paper1);
        paragraphRepository.save(paragraph1);

        Paragraph paragraph2 = new Paragraph();
        paragraph2.setType("Type B");
        paragraph2.setSection("Section 2");
        paragraph2.setSecNum("2");
        paragraph2.setText("Paragraph text 2");
        paragraph2.setPaper(paper2);
        paragraphRepository.save(paragraph2);

        // Create paragraph-reference relationships
        ParagraphReference paragraphReference1 = new ParagraphReference();
        paragraphReference1.setParagraph(paragraph1);
        paragraphReference1.setReference(reference1);
        paragraphReference1.setText("Text referring to reference 1 in paragraph 1");
        paragraphReference1.setStart(0);
        paragraphReference1.setEnd(10);
        paragraphReferenceRepository.save(paragraphReference1);

        ParagraphReference paragraphReference2 = new ParagraphReference();
        paragraphReference2.setParagraph(paragraph2);
        paragraphReference2.setReference(reference2);
        paragraphReference2.setText("Text referring to reference 2 in paragraph 2");
        paragraphReference2.setStart(0);
        paragraphReference2.setEnd(15);
        paragraphReferenceRepository.save(paragraphReference2);

    }

    @Test
    public void Test2() {
        Paper paper = paperRepository.findById(1L).orElse(null);
        if (paper != null) {
            System.out.println("Authors of Paper '" + paper.getTitle() + "':");
            paper.getAuthors().forEach(paperAuthor -> System.out.println("- " + paperAuthor.getAuthor().getName()));
        }
    }

    @Test
    public void Test3(){
        Paper paper = paperRepository.findById(1L).orElse(null);
        if (paper != null) {
            System.out.println("References in Paper '" + paper.getTitle() + "':");
            paper.getReferences().forEach(paperReference -> System.out.println("- " + paperReference.getReference().getTitle()));
        }
    }

    @Test
    public void Test4(){
        Paper paper = paperRepository.findById(1L).orElse(null);
        if (paper != null) {
            System.out.println("Paragraphs in Paper '" + paper.getTitle() + "':");
            paper.getParagraphs().forEach(paragraph -> {
                System.out.println("- Paragraph '" + paragraph.getSection() + "':");
                System.out.println("  Text: " + paragraph.getText());
                System.out.println("  References:");
                paragraph.getReferences().forEach(paragraphReference -> {
                    System.out.println("  - " + paragraphReference.getReference().getTitle());
                });
            });
        }
    }
}
