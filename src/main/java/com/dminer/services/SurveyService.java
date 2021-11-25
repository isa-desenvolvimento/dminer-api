package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Survey;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.SurveyRepository;
import com.dminer.services.interfaces.ISurveyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class SurveyService implements ISurveyService {

    @Autowired
	private SurveyRepository surveyRepository;	
	
	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;	

	@Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;	


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


	public Optional<List<Survey>> searchPostgres(String keyword) {		
		return Optional.ofNullable(genericRepositoryPostgres.searchSurvey(keyword));
    }

	public Optional<List<Survey>> searchSqlServer(String keyword) {
		return Optional.ofNullable(genericRepositorySqlServer.searchSurvey(keyword));
    }
}
