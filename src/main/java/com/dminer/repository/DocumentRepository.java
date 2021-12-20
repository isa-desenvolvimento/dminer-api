package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Document;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    
    List<Document> findAllByOrderByCreateDateAsc();

    //findAllByOrderByIdAsc
}
