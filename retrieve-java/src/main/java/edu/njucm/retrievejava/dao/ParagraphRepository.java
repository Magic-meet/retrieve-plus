package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.Paper;
import edu.njucm.retrievejava.model.Paragraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends CrudRepository<Paragraph, Long> {
    List<Paragraph> findAllByPaper(Paper paper);

    void deleteAllByPaper(Paper paper);
}
