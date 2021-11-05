package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Survey;

public interface ISurveyService {
    
	Survey persist(Survey survey);
	
	Optional<Survey> findById(int id);
	
	Optional<List<Survey>> findAll();

	void delete(int id);

}
