package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.PaperInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaperInfoRepository extends CrudRepository<PaperInfo, Long> {
    List<PaperInfo> findAllByOrderByUploadTimeDesc();
}
