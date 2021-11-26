package com.dminer.converters;

import com.dminer.dto.SurveyDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.dto.SurveyResponseDTO;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyConverter {

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private UserService userService;

    
    public SurveyResponseDTO surveyResponseToDTO(SurveyResponses survey) { 
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(survey.getId());
        dto.setIdSurvey(survey.getIdSurvey());
        dto.setCountA(survey.getCountA());
        dto.setCountB(survey.getCountB());
        survey.getUsers().forEach(u -> {
            dto.getUsers().add(u.getLogin());
        });
        return dto;
    }


    public SurveyDTO entityToDTO(Survey survey) { 
        SurveyDTO dto = new SurveyDTO();
        dto.setId(survey.getId());
        dto.setDate(survey.getDate() != null ? UtilDataHora.timestampToString(survey.getDate()) : null);
        dto.setOptionA(survey.getOptionA());
        dto.setOptionB(survey.getOptionB());
        dto.setQuestion(survey.getQuestion());
        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(survey.getId());
        dto.setCountA(findByIdSurvey.getCountA());
        dto.setCountB(findByIdSurvey.getCountB());
        return dto;
    }

    public Survey dtoToEntity(SurveyDTO dto) {
        Survey survey = new Survey();
        survey.setId(dto.getId());
        survey.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        survey.setOptionA(dto.getOptionA());        
        survey.setOptionB(dto.getOptionB());
        survey.setQuestion(dto.getQuestion());
        return survey;
    }

    public Survey requestDtoToEntity(SurveyRequestDTO dto) {
        Survey survey = new Survey();
        survey.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        survey.setOptionA(dto.getOptionA());
        survey.setOptionB(dto.getOptionB());
        survey.setQuestion(dto.getQuestion());
        return survey;
    }
}
