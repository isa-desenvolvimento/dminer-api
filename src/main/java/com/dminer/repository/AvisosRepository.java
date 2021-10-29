package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Avisos;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AvisosRepository extends JpaRepository<Avisos, Integer> {
    
}
