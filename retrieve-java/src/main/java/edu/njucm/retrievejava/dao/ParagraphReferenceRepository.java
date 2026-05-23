package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.ParagraphReference;
import edu.njucm.retrievejava.model.Paragraph;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParagraphReferenceRepository extends CrudRepository<ParagraphReference, Long> {
    void deleteAllByParagraph(Paragraph paragraph);
}
