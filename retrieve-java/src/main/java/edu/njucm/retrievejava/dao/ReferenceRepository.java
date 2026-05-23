package edu.njucm.retrievejava.dao;

import edu.njucm.retrievejava.model.Reference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferenceRepository extends CrudRepository<Reference, Long> {
}