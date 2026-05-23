package edu.njucm.retrievejava.vo;

import java.util.ArrayList;
import java.util.List;

public class RetrievalResponseVO {
    private String mode;
    private String query;
    private Integer total;
    private List<RetrievalResultItemVO> results = new ArrayList<>();

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<RetrievalResultItemVO> getResults() {
        return results;
    }

    public void setResults(List<RetrievalResultItemVO> results) {
        this.results = results;
    }
}
