package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperReference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperReferenceRepository extends CrudRepository<PaperReference, Long> {
    List<PaperReference> findAllByPaper(Paper paper);

    void deleteAllByPaper(Paper paper);
}
