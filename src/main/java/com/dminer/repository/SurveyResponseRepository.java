package com.dminer.repository;

import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyResponseRepository extends JpaRepository<SurveyResponses, Integer> {
    
    @Query("SELECT s FROM SurveyResponses s WHERE s.idSurvey = :idSurvey")
    public SurveyResponses findByIdSurvey(@Param("idSurvey") int idSurvey);
    
}
