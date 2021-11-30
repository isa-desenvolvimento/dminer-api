package com.dminer.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.SurveyConverter;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.dto.SurveyResponseDTO;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.response.Response;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;

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
    private SurveyConverter surveyConverter;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private Environment env;



    private void validateRequestDto(SurveyRequestDTO surveyRequestDto, BindingResult result) {        
        if (surveyRequestDto.getQuestion() == null || surveyRequestDto.getQuestion().isEmpty()) {
            result.addError(new ObjectError("SurveyRequestDTO", "Questão precisa estar preenchida."));
        } 

        if (surveyRequestDto.getOptionA() == null || surveyRequestDto.getOptionA().isEmpty()) {
            result.addError(new ObjectError("SurveyRequestDTO", "Opção A precisa estar preenchido."));			
        }

        if (surveyRequestDto.getOptionB() == null || surveyRequestDto.getOptionB().isEmpty()) {
            result.addError(new ObjectError("SurveyRequestDTO", "Opção B precisa estar preenchido."));
        } else {
            try {
                Timestamp.valueOf(surveyRequestDto.getDate());
            } catch (IllegalArgumentException e) {
                result.addError(new ObjectError("SurveyRequestDTO", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss."));
            }
        }
    }


    private Response<String> validateAnswerQuestion( Integer id, String loginUser, String option) {
        Response<String> response = new Response<>();
        if (id == null) {
            log.info("Informe o id do questionário");
            response.getErrors().add("Informe o id do questionário");
        } else {
            Optional<Survey> findById = surveyService.findById(id);
            if (!findById.isPresent()) {
                log.info("Questionário não encontrado");
                response.getErrors().add("Questionário não encontrado");
            }
        }
        
        if (loginUser == null) {
            log.info("Informe o login do usuário que está respondendo o questionário");
            response.getErrors().add("Informe o login do usuário que está respondendo o questionário");
        } 
        
        if (option == null || option.isEmpty() || (!option.equalsIgnoreCase("A") && !option.equalsIgnoreCase("B"))) {
            log.info("Informe uma opção válida para a resposta = {}", option);
            response.getErrors().add("Informe uma opção válida para a resposta");
        }

        return response;
    }



    @PostMapping()
    public ResponseEntity<Response<SurveyDTO>> create( @Valid @RequestBody SurveyRequestDTO surveyRequestDto, BindingResult result) {

        Response<SurveyDTO> response = new Response<>();

        validateRequestDto(surveyRequestDto, result);
        if (result.hasErrors()) {
            log.info("Erro validando surveyRequestDto: {}", surveyRequestDto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Survey survey = surveyService.persist(surveyConverter.requestDtoToEntity(surveyRequestDto));

        SurveyResponses s = new SurveyResponses();
        s.setIdSurvey(survey.getId());

        surveyResponseRepository.save(s);

        response.setData(surveyConverter.entityToDTO(survey));
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/answer/{idSurvey}/{idUser}/{option}")
    public ResponseEntity<Response<String>> answerQuestion( @PathVariable("idSurvey") Integer id, @PathVariable("loginUser") String loginUser, @PathVariable("option") String option) {

        Response<String> response = validateAnswerQuestion(id, loginUser, option);
        if (! response.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> userOpt = userService.findByLogin(loginUser);
        if (!userOpt.isPresent()) {
            response.getErrors().add("Nenhum usuário encontrado com id " + loginUser);
            return ResponseEntity.badRequest().body(response);
        }

        User user = userOpt.get();
        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        if (findByIdSurvey == null) {
            response.getErrors().add("Nenhum questionário encontrado com id " + id);
            return ResponseEntity.badRequest().body(response);
        }
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
        
        try {
            surveyResponseRepository.save(findByIdSurvey);
        } catch (DataIntegrityViolationException e) {
            log.error("Questionário já foi respondido por este usuário");
            response.getErrors().add("Questionário já foi respondido por este usuário");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData("Questionário respondido com sucesso!");
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/count/{idSurvey}")
    public ResponseEntity<Response<SurveyResponseDTO>> getCount(@PathVariable("idSurvey") Integer id) {
        
        Response<SurveyResponseDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        if (findByIdSurvey == null) {
            response.getErrors().add("Questionário de id: "+ id +", não encontrado!");
            return ResponseEntity.badRequest().body(response);
        }

        System.out.println(findByIdSurvey.toString());

        response.setData(surveyConverter.surveyResponseToDTO(findByIdSurvey));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<SurveyDTO>> put( @RequestBody SurveyDTO surveyDto, BindingResult result ) {

        log.info("Alterando um questionário {}", surveyDto);

        Response<SurveyDTO> response = new Response<>();

        Optional<Survey> survey = surveyService.findById(surveyDto.getId());
        if (! survey.isPresent()) {
            response.getErrors().add("Questionário de id: "+ surveyDto.getId() +", não encontrado!");
            return ResponseEntity.badRequest().body(response);
        }

        Survey s = survey.get();
        SurveyResponses responseDto = surveyResponseRepository.findByIdSurvey(surveyDto.getId());
        if (responseDto != null) {
            surveyDto.setCountA(responseDto.getCountA());
            surveyDto.setCountB(responseDto.getCountB());
        }
        s = surveyService.persist(surveyConverter.dtoToEntity(surveyDto));
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


    @GetMapping(value = "/all")
    public ResponseEntity<Response<List<SurveyDTO>>> getAll() {
        
        Response<List<SurveyDTO>> response = new Response<>();

        Optional<List<Survey>> surveys = surveyService.findAll();
        if (surveys.get().isEmpty()) {
            response.getErrors().add("Questionários não encontrados");
            return ResponseEntity.badRequest().body(response);
        }

        List<SurveyDTO> surveysDto = new ArrayList<>();
        surveys.get().forEach(u -> {
            surveysDto.add(surveyConverter.entityToDTO(u));
        });
        response.setData(surveysDto);
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

        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        if (findByIdSurvey != null) {
            surveyResponseRepository.deleteById(findByIdSurvey.getId());
        }
        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


    
    

    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
