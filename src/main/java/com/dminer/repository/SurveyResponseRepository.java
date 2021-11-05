package com.dminer.repository;

import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponses, Integer> {
    
    public SurveyResponses findByIdSurvey(int idSurvey);
    
}
