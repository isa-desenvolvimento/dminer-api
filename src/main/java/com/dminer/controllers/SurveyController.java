package com.dminer.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.SurveyConverter;
import com.dminer.dto.SurveyDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.dto.SurveyResponseDTO;
import com.dminer.entities.Survey;
import com.dminer.entities.SurveyResponses;
import com.dminer.entities.User;
import com.dminer.repository.SurveyResponseRepository;
import com.dminer.repository.SurveyResponseUsersRepository;
import com.dminer.response.Response;
import com.dminer.services.SurveyService;
import com.dminer.services.UserService;


@RestController
@RequestMapping("/survey")
@CrossOrigin(origins = "*")
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
    private SurveyResponseUsersRepository surveyResponseUsersRepository;

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
            response.addError("Informe o id do questionário");
        } else {
            Optional<Survey> findById = surveyService.findById(id);
            if (!findById.isPresent()) {
                log.info("Questionário não encontrado");
                response.addError("Questionário não encontrado");
            }
        }
        
        if (loginUser == null) {
            log.info("Informe o login do usuário que está respondendo o questionário");
            response.addError("Informe o login do usuário que está respondendo o questionário");
        } 
        
        if (option == null || option.isEmpty() || (!option.equalsIgnoreCase("A") && !option.equalsIgnoreCase("B"))) {
            log.info("Informe uma opção válida para a resposta = {}", option);
            response.addError("Informe uma opção válida para a resposta");
        }

        return response;
    }



    @PostMapping()
    public ResponseEntity<Response<SurveyDTO>> create( @Valid @RequestBody SurveyRequestDTO surveyRequestDto, BindingResult result) {

        Response<SurveyDTO> response = new Response<>();

        validateRequestDto(surveyRequestDto, result);
        if (result.hasErrors()) {
            log.info("Erro validando surveyRequestDto: {}", surveyRequestDto);
            result.getAllErrors().forEach( e -> response.addError(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Survey survey = surveyService.persist(surveyConverter.requestDtoToEntity(surveyRequestDto));

        SurveyResponses s = new SurveyResponses();
        s.setIdSurvey(survey.getId());

        surveyResponseRepository.save(s);

        response.setData(surveyConverter.entityToDTO(survey));
        return ResponseEntity.ok().body(response);
    }


    @PostMapping("/answer/{idSurvey}/{login}/{option}")
    public ResponseEntity<Response<String>> answerQuestion( @PathVariable("idSurvey") Integer id, @PathVariable("login") String loginUser, @PathVariable("option") String option, @RequestParam(name = "unique", required = false, defaultValue = "false") Boolean unique) {

        Response<String> response = validateAnswerQuestion(id, loginUser, option);
        if (response.containErrors()) {
            return ResponseEntity.badRequest().body(response);
        }

        // Optional<Survey> opt = surveyService.findById(id);
        // Survey survey = opt.get();

        User newUser;
        if (!userService.existsByLogin(loginUser)) {
        	newUser = userService.persist(new User(loginUser, ""));
        } else {
        	Optional<User> userOpt = userService.findByLogin(loginUser);
        	newUser = userOpt.get();
        }
        
        SurveyResponses surveyResponse = surveyResponseRepository.findByIdSurvey(id);
        if (surveyResponse == null) {
            surveyResponse = new SurveyResponses();
            surveyResponse.setIdSurvey(id);
            surveyResponse.getUsers().add(newUser);
            surveyResponse = surveyResponseRepository.save(surveyResponse);
        }
        
        surveyResponseUsersRepository.persist(surveyResponse.getId(), newUser.getId());
        
        if (option.equalsIgnoreCase("a")) {
            surveyResponse.setCountA(
                surveyResponse.getCountA() + 1
            );
        } else {
            surveyResponse.setCountB(
                surveyResponse.getCountB() + 1
            );
        }
        
        try {
            surveyResponseRepository.save(surveyResponse);
        } catch (DataIntegrityViolationException e) {
            log.error("Questionário já foi respondido por este usuário");
            response.addError("Questionário já foi respondido por este usuário");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData("Questionário respondido com sucesso!");
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/count/{idSurvey}")
    public ResponseEntity<Response<SurveyResponseDTO>> getCount(@PathVariable("idSurvey") Integer id) {
        
        Response<SurveyResponseDTO> response = new Response<>();
        if (id == null) {
            response.addError("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        SurveyResponses findByIdSurvey = surveyResponseRepository.findByIdSurvey(id);
        if (findByIdSurvey == null) {
            response.addError("Questionário de id: "+ id +", não encontrado!");
            return ResponseEntity.badRequest().body(response);
        }
        
        response.setData(surveyConverter.surveyResponseToDTO(findByIdSurvey));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<SurveyDTO>> put( @RequestBody SurveyDTO surveyDto, BindingResult result ) {

        log.info("Alterando um questionário {}", surveyDto);

        Response<SurveyDTO> response = new Response<>();

        Optional<Survey> survey = surveyService.findById(surveyDto.getId());
        if (! survey.isPresent()) {
            response.addError("Questionário de id: "+ surveyDto.getId() +", não encontrado!");
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
            response.addError("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Survey> user = surveyService.findById(id);
        if (!user.isPresent()) {
            response.addError("Questionário não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(surveyConverter.entityToDTO(user.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all/{login}")
    public ResponseEntity<Response<List<SurveyDTO>>> getAll(@PathVariable("login") String login) {
        
        Response<List<SurveyDTO>> response = new Response<>();

        Optional<List<Survey>> surveysOpt = surveyService.findAll();
        if (surveysOpt.get().isEmpty()) {
            response.addError("Questionários não encontrados");
            return ResponseEntity.badRequest().body(response);
        }

        List<Survey> surveys = surveysOpt.get();
        surveys = surveys.stream()
		.sorted(Comparator.comparing(Survey::getDate).reversed())
		.collect(Collectors.toList());


        List<SurveyDTO> surveysDto = new ArrayList<>();
        surveys.forEach(u -> {
            SurveyDTO dto = surveyConverter.entityToDTO(u);
            SurveyResponses responseDto = surveyResponseRepository.findByIdSurvey(dto.getId());
            
            if (responseDto != null) {
                User user = responseDto.getUsers().stream().
                filter(f -> f.getLogin().equalsIgnoreCase(login)).
                findAny().
                orElse(null);
    
                if (user != null) {
                    dto.setVoted(true);
                }
            }
            surveysDto.add(dto);
        });

        response.setData(surveysDto);
        return ResponseEntity.ok().body(response);
    }



    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.addError("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {surveyService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.addError("Questionário não encontrado");
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
