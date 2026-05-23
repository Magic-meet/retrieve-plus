package edu.njucm.retrievejava.es.dao;


import edu.njucm.retrievejava.es.model.PaperES;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


import java.util.List;


@Repository
public interface PaperElasticsearchRepository extends ElasticsearchRepository<PaperES,String> {
    Boolean deleteByPaperId(Long paperId);

    PaperES findByPaperId(Long paperId);

    List<PaperES> searchByTitle(String title);

    @Query("{\"function_score\": {\"query\": {\"bool\": {\"filter\": [{\"exists\": {\"field\": \"embedding\"}}]}},\"functions\": [{\"script_score\": {\"script\": {\"source\": \"cosineSimilarity(params.queryVector, 'embedding') + 1.0\",\"params\": {\"queryVector\": ?0}}}}],\"boost_mode\": \"replace\"}}")
    List<PaperES> searchByEmbeddingNLP(List<Double> queryVector);

    List<PaperES> searchByAbstractContent(String abstractContent);
}
