package com.dminer.repository;

import com.dminer.entities.Survey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey, Integer> {
    
}
