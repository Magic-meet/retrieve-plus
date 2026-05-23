package edu.njucm.retrievejava.es.service;



import com.fasterxml.jackson.core.JsonProcessingException;
import edu.njucm.retrievejava.es.model.PaperES;
import edu.njucm.retrievejava.vo.PaperVO;
import edu.njucm.retrievejava.vo.ResultEnum;

import java.util.List;

public interface SearchService {
    List<PaperVO> search(String type, String content) throws JsonProcessingException;
    List<PaperVO> recommend(Long paperId,Long authorId,Integer count,Integer offset) throws JsonProcessingException;
}
