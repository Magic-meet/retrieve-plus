package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.Paper;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaperRepository extends CrudRepository<Paper, Long> {
    Paper findByPaperId(Long paperId);

    Paper findByPaperInfoRelationId(Long relationId);
}
