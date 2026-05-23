package edu.njucm.retrievejava.dao;


import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.PaperAuthor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PaperAuthorRepository extends CrudRepository<PaperAuthor, Long> {
    List<PaperAuthor> findByPaper(Paper paper);

    void deleteAllByPaper(Paper paper);
}
