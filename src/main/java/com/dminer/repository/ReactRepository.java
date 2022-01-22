package com.dminer.repository;

import com.dminer.entities.React;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactRepository extends JpaRepository<React, Integer> {
    
    React findByReact(String react);
}
