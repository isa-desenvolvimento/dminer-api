package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.dto.UserDTO;
import com.dminer.entities.Survey;
import com.dminer.repository.EventsTimeRepositoryPostgres;
import com.dminer.repository.EventsTimeRepositorySqlServer;
import com.dminer.repository.SurveyRepository;
import com.dminer.repository.UserRepository;
import com.dminer.services.interfaces.ISurveyService;
import com.dminer.services.interfaces.IUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SurveyService implements ISurveyService {

    @Autowired
	private SurveyRepository surveyRepository;	
	
	private static final Logger log = LoggerFactory.getLogger(SurveyService.class);


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
