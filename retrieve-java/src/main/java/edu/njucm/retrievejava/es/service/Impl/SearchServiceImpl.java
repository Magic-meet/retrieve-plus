package edu.njucm.retrievejava.es.service.Impl;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.njucm.retrievejava.dao.PaperAuthorRepository;
import edu.njucm.retrievejava.dao.PaperReferenceRepository;
import edu.njucm.retrievejava.dao.PaperRepository;
import edu.njucm.retrievejava.es.dao.PaperElasticsearchRepository;
import edu.njucm.retrievejava.es.dao.ParagraphElasticsearchRepository;
import edu.njucm.retrievejava.es.model.PaperES;
import edu.njucm.retrievejava.es.model.ParagraphES;
import edu.njucm.retrievejava.es.service.SearchService;
import edu.njucm.retrievejava.model.Author;
import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperAuthor;
import edu.njucm.retrievejava.model.PaperReference;
import edu.njucm.retrievejava.service.RPCService;
import edu.njucm.retrievejava.untils.ServiceTools;
import edu.njucm.retrievejava.vo.PaperVO;
import edu.njucm.retrievejava.vo.ResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private PaperElasticsearchRepository paperElasticsearchRepository;

    @Autowired
    private ParagraphElasticsearchRepository paragraphElasticsearchRepository;

    @Autowired
    private PaperRepository paperRepository;

    @Autowired
    private PaperReferenceRepository paperReferenceRepository;


    @Autowired
    private RPCService rpcService;
    @Autowired
    private PaperAuthorRepository paperAuthorRepository;

    public List<PaperVO> search(String type, String content) throws JsonProcessingException {
        List<PaperES> paperESList = List.of();

        switch (type){
            case("title"):{
                paperESList = paperElasticsearchRepository.searchByTitle(content);
                break;
            }
            case ("abstract"):{
                paperESList = paperElasticsearchRepository.searchByAbstractContent(content);
                break;
            }
            case ("nlp"):{
                paperESList = paperElasticsearchRepository.searchByEmbeddingNLP(Arrays.stream(rpcService.processString(content)).boxed().collect(Collectors.toList()));
                break;
            }
            case ("all"):{
                List<ParagraphES> paragraphESList = paragraphElasticsearchRepository.findByText(content);
                Set<Long> uniquePaperIds = new HashSet<>();
                for (ParagraphES paragraph : paragraphESList) {
                    uniquePaperIds.add(paragraph.getPaperId());
                }
                List<Long> paperIdList = new ArrayList<>(uniquePaperIds);
                for (Long paperId : paperIdList) {
                    paperESList = new ArrayList<>();
                    paperESList.add(paperElasticsearchRepository.findByPaperId(paperId));
                }
            }
        }
        if (paperESList != null){
            return paperES2VOList(paperESList);
        }
        return null;
    }

    public List<PaperVO> recommend(Long paperId,Long authorId,Integer count,Integer offset) throws JsonProcessingException {
        if (paperId != null) {
            Paper paper= paperRepository.findByPaperId(paperId);
            List<PaperReference> paperReferenceList = paperReferenceRepository.findAllByPaper(paper);
            List<String> paperTitles = new ArrayList<>();
            List<PaperES> paperESList = List.of();
            paperESList = paperElasticsearchRepository.searchByEmbeddingNLP(Arrays.stream(rpcService.processString(paper.getTitle())).boxed().collect(Collectors.toList()));
            return ServiceTools.paginate(paperES2VOList(paperESList),count,offset);
        }
        return null;
    }

    public List<PaperVO> paperES2VOList(List<PaperES> paperESList) {
        List<PaperVO> paperVOList = new java.util.ArrayList<>();
        for (PaperES paperES : paperESList){
            PaperVO paperVO = new PaperVO();
            paperVO.setTitle(paperES.getTitle());
            paperVO.setVenue(paperES.getVenue());
            paperVO.setYear(paperES.getYear());
            paperVO.setKeywords(paperES.getKeywords());
            paperVO.setAbstractContent(paperES.getAbstractContent());
            paperVO.setPaperId(paperES.getPaperId());
            Paper paper = paperRepository.findByPaperId(paperES.getPaperId());
            Set<PaperAuthor> paperAuthors = paper.getAuthors();
            List<PaperAuthor> paperAuthorList = new ArrayList<>(paperAuthors);
            StringBuilder authors = new StringBuilder();
            for (int i = 0; i < paperAuthorList.size(); i++) {
                authors.append(paperAuthorList.get(i).getAuthor().getName());
                if (i < paperAuthorList.size() - 1) {
                    authors.append("; ");
                }
            }
            paperVO.setAuthors(authors.toString());
            paperVOList.add(paperVO);
        }
        return paperVOList;
    }




}



