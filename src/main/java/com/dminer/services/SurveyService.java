package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Survey;
import com.dminer.repository.SurveyRepository;
import com.dminer.services.interfaces.ISurveyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SurveyService implements ISurveyService {

    @Autowired
	private SurveyRepository surveyRepository;	
	
    @Override
    public Survey persist(Survey user) {
		return surveyRepository.save(user);
    }

    @Override
    public Optional<Survey> findById(int id) {
		return surveyRepository.findById(id);
    }

    @Override
    public Optional<List<Survey>> findAll() {
		return Optional.ofNullable(surveyRepository.findAll());
    }

    @Override
    public void delete(int id) throws EmptyResultDataAccessException {
		surveyRepository.deleteById(id);
    }
}
