package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.SurveyDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.entities.Survey;
import com.dminer.entities.User;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SurveyConverter {

    @Autowired
    private UserService userService;


    public SurveyDTO entityToDTO(Survey survey) { 
        SurveyDTO dto = new SurveyDTO();
        dto.setId(survey.getId());
        dto.setDate(survey.getDate() != null ? UtilDataHora.timestampToString(survey.getDate()) : null);
        // dto.setIdUser(survey.getUser().getId());
        dto.setOptionA(survey.getOptionA());
        dto.setOptionB(survey.getOptionB());
        dto.setQuestion(survey.getQuestion());
        return dto;
    }

    public Survey dtoToEntity(SurveyDTO dto) {
        Survey survey = new Survey();
        survey.setId(dto.getId());
        survey.setDate(survey.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        Optional<User> user = userService.findById(dto.getIdUser());
        // if (user.isPresent())
        //     survey.setUser(user.get());
        survey.setOptionA(dto.getOptionA());        
        survey.setOptionB(dto.getOptionB());        
        survey.setQuestion(dto.getQuestion());
        return survey;
    }

    public Survey requestDtoToEntity(SurveyRequestDTO dto) {
        Survey survey = new Survey();
        survey.setDate(survey.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        // Optional<User> user = userService.findById(dto.getIdUser());
        // if (user.isPresent())
            // survey.setUser(user.get());
        survey.setOptionA(dto.getOptionA());
        survey.setOptionB(dto.getOptionB());
        survey.setQuestion(dto.getQuestion());
        return survey;
    }
}
