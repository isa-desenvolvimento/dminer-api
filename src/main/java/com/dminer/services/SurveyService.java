package com.dminer.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dminer.dto.SurveyDTO;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.SurveyRepository;
import com.dminer.repository.SurveyResponseRepository;
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

	@Autowired
    private SurveyResponseRepository surveyResponseRepository;
	

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
		return Optional.ofNullable(surveyRepository.findAllByOrderByDateDesc());
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

	public List<SurveyDTO> search(String keyword, String login, boolean isProd) {
        List<Survey> result = new ArrayList<>();
        if (keyword != null) {
            if (isProd) {
				result = genericRepositorySqlServer.searchSurvey(keyword);
            } else {
                result = genericRepositoryPostgres.searchSurvey(keyword);
            }          
        } else {
            result = surveyRepository.findAll();
        }

		List<SurveyDTO> dtoList = new ArrayList<>();
		
		result.forEach(u -> {
			
			SurveyDTO dto = new SurveyDTO();
			SurveyResponses responseDto = surveyResponseRepository.findByIdSurvey(u.getId());
			if (responseDto != null) {
				dto = u.convertDto(responseDto);
			} else {
				dto = u.convertDto();	
			}

			if (responseDto != null) {
				User user = responseDto.getUsers().stream().
				filter(f -> f.getLogin().equalsIgnoreCase(login)).
				findAny().
				orElse(null);
	
				if (user != null) {
					dto.setVoted(true);
				}
			}
			dtoList.add(dto);
		});
        return dtoList;
    }
}
