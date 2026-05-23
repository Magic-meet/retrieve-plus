package edu.njucm.retrievejava.es.dao;


import edu.njucm.retrievejava.es.model.ParagraphES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ParagraphElasticsearchRepository extends ElasticsearchRepository <ParagraphES, String> {
    Boolean deleteByParagraphId(Long paragraphId);

    List<ParagraphES> findByText(String text);

}
