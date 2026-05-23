package edu.njucm.retrievejava.untilsTest;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.njucm.retrievejava.dao.*;
import edu.njucm.retrievejava.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.njucm.retrievejava.untils.JsonParser.extractAuthors;
import static edu.njucm.retrievejava.untils.JsonParser.extractPaper;

@SpringBootTest
public class JsonParser {

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
    public void Test(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;
        try {
            root = mapper.readTree(new File("/home/zj/Dev/Sys/Projects/retrieve_new/retrieve-python/grobid2json/out_dir/KGAT Knowledge Graph Attention Network for.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 作者表
        List<Author> authors = extractAuthors(root);
        //论文实体
        Paper paper = extractPaper(root);
        //论文-作者关系表
        List<PaperAuthor> paperAuthors = new ArrayList<>();
        for (int i=0;i<authors.size();){
            paperAuthors.add(new PaperAuthor(null,paper,authors.get(i),++i));
        }
        //段落、段落-引用关系表
        Map<String, Object> objectMap = edu.njucm.retrievejava.untils.JsonParser.extractParagraphs(root);
        List<Paragraph> paragraphs = (List<Paragraph>) objectMap.get("paragraphs");
        // 段落关系补充 段落与论文相关联
        for (Paragraph paragraph : paragraphs){
            paragraph.setPaper(paper);
        }
        //段落引用
        List<ParagraphReference> paragraphReferences = (List<ParagraphReference>) objectMap.get("paragraphReferences");
        // 引用、论文-引用关系表
        objectMap = edu.njucm.retrievejava.untils.JsonParser.PaperReferences(root);
        //论文引用
        List<PaperReference> paperReferences = (List<PaperReference>) objectMap.get("paperReferences");
        for (PaperReference paperReference : paperReferences){
            paperReference.setPaper(paper);
        }
        List<Reference> references = (List<Reference>) objectMap.get("references");
        //更新引用关系
        edu.njucm.retrievejava.untils.JsonParser.updateParagraphReferences(paperReferences,paragraphReferences);
        paperRepository.save(paper);
        authorRepository.saveAll(authors);
        paragraphRepository.saveAll(paragraphs);
        referenceRepository.saveAll(references);
        paperAuthorRepository.saveAll(paperAuthors);
        paperReferenceRepository.saveAll(paperReferences);
        paragraphReferenceRepository.saveAll(paragraphReferences);
    }
}
