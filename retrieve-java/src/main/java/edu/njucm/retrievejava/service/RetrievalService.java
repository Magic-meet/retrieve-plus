package edu.njucm.retrievejava.service;

import edu.njucm.retrievejava.vo.RetrievalRequestVO;
import edu.njucm.retrievejava.vo.RetrievalResponseVO;

import java.io.IOException;

public interface RetrievalService {
    RetrievalResponseVO keywordSearch(RetrievalRequestVO request) throws IOException;

    RetrievalResponseVO semanticSearch(RetrievalRequestVO request) throws IOException;

    RetrievalResponseVO hybridSearch(RetrievalRequestVO request) throws IOException;
}
