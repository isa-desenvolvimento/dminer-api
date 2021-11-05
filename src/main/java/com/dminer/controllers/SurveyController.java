package com.dminer.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.constantes.Constantes;
import com.dminer.converters.SurveyConverter;
import com.dminer.converters.UserConverter;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.dto.SurveyResponseDTO;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.FileInfo;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.entities.Survey;
import com.dminer.response.Response;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/survey")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SurveyController {

    private static final Logger log = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private SurveyConverter surveyConverter;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private Environment env;

    private void validateRequestDto(UserRequestDTO userRequestDTO, BindingResult result) {
        if (userRequestDTO.getName() == null) {
            result.addError(new ObjectError("userRequestDTO", "Nome precisa estar preenchido."));			
		}
        if (userRequestDTO.getDtBirthday() == null) {
            result.addError(new ObjectError("userRequestDTO", "Data de aniversário precisa estar preenchido."));
		}
    }

    
    
    


    //@PostMapping(consumes = {"multipart/form-data", "multipart/form-data", "application/json"})
    @PostMapping()
    public ResponseEntity<Response<SurveyDTO>> create( @RequestBody SurveyRequestDTO surveyRequestDto) {

        Response<SurveyDTO> response = new Response<>();

        Survey survey = surveyService.persist(surveyConverter.requestDtoToEntity(surveyRequestDto));
        
        SurveyResponses s = new SurveyResponses();
        s.setIdSurvey(survey.getId());

        surveyResponseRepository.save(s);

        response.setData(surveyConverter.entityToDTO(survey));
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/answer/{idSurvey}/{idUser}/{option}")
    public ResponseEntity<Boolean> answerQuestion( @PathVariable("idSurvey") Integer id, @PathVariable("idUser") Integer idUser, @PathVariable("option") String option) {

        Optional<User> userOpt = userService.findById(id);
        User user = userOpt.get();

        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        findByIdSurvey.getUsers().add(user);

        if (option.equalsIgnoreCase("a")) {
            findByIdSurvey.setCountA(
                findByIdSurvey.getCountA() + 1
            );
        } else {
            findByIdSurvey.setCountB(
                findByIdSurvey.getCountB() + 1
            );
        }

        surveyResponseRepository.save(findByIdSurvey);
        return ResponseEntity.ok().body(true);
    }


    @GetMapping(value = "/count/{idSurvey}")
    public ResponseEntity<Response<SurveyResponseDTO>> getCount(@PathVariable("idSurvey") Integer id) {
        
        Response<SurveyResponseDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        response.setData(surveyConverter.surveyResponseToDTO(findByIdSurvey));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<SurveyDTO>> put( @RequestBody SurveyDTO userDto) {

        log.info("Alterando um usuário {}", userDto);

        Response<SurveyDTO> response = new Response<>();

        Optional<Survey> survey = surveyService.findById(userDto.getId());
        Survey s = survey.get();
        s = surveyConverter.dtoToEntity(userDto);
        s = surveyService.persist(s);
        response.setData(surveyConverter.entityToDTO(s));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<SurveyDTO>> get(@PathVariable("id") Integer id) {
        
        Response<SurveyDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Survey> user = surveyService.findById(id);
        if (!user.isPresent()) {
            response.getErrors().add("Questionário não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(surveyConverter.entityToDTO(user.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping()
    public ResponseEntity<Response<List<SurveyDTO>>> getAll() {
        
        Response<List<SurveyDTO>> response = new Response<>();

        Optional<List<Survey>> user = surveyService.findAll();
        if (user.get().isEmpty()) {
            response.getErrors().add("Usuários não encontrados");
            return ResponseEntity.badRequest().body(response);
        }

        List<SurveyDTO> usuarios = new ArrayList<>();
        user.get().forEach(u -> {
            usuarios.add(surveyConverter.entityToDTO(u));
        });
        response.setData(usuarios);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {surveyService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Questionário não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


    
    

    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
