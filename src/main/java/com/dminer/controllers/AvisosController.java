package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.AvisosConverter;
import com.dminer.dto.AvisosDTO;
import com.dminer.dto.AvisosRequestDTO;
import com.dminer.entities.Avisos;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.AvisosService;
import com.dminer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/avisos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AvisosController {
    
    private static final Logger log = LoggerFactory.getLogger(AvisosController.class);

    @Autowired 
    private AvisosConverter avisosConverter;

    @Autowired
    private UserService userService;

    @Autowired
    private AvisosService avisosService;
    

    private void validateRequestDto(AvisosRequestDTO avisosRequestDTO, BindingResult result) {
        if (avisosRequestDTO.getUsuarios() == null) {
            result.addError(new ObjectError("AvisosRequestDTO", "Id do usuário precisa estar preenchido."));
		} else {
            avisosRequestDTO.getUsuarios().forEach(id -> {
                Optional<User> findById = userService.findById(id);
                if (!findById.isPresent()) {
                    result.addError(new ObjectError("AvisosRequestDTO", "Usuário: "+id+" não encontrado."));
                }
            });
        }

        if (avisosRequestDTO.getPrioridade() == null || avisosRequestDTO.getPrioridade().isEmpty()) {
            result.addError(new ObjectError("AvisosRequestDTO", "Prioridade do aviso precisa estar preenchido."));			
		}

        if (avisosRequestDTO.getCriador() == null || avisosRequestDTO.getCriador().isEmpty()) {
            result.addError(new ObjectError("AvisosRequestDTO", "Criador do aviso precisa estar preenchido."));			
		}

        if (avisosRequestDTO.getData()== null || avisosRequestDTO.getData().isEmpty()) {
            result.addError(new ObjectError("AvisosRequestDTO", "Data do aviso precisa estar preenchido."));			
		}
    }
    

    @PostMapping
    public ResponseEntity<Response<AvisosDTO>> create(@Valid @RequestBody AvisosRequestDTO avisosRequest, BindingResult result) {
    
        log.info("Salvando um novo avisos {}", avisosRequest);

		Response<AvisosDTO> response = new Response<>();
        validateRequestDto(avisosRequest, result);
        if (result.hasErrors()) {
            log.info("Erro validando avisosRequest: {}", avisosRequest);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Avisos events = avisosService.persist(avisosConverter.requestDtoToEntity(avisosRequest));
        response.setData(avisosConverter.entityToDTO(events));

        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<AvisosDTO>> getNotifications(@PathVariable("id") Integer id) {
        log.info("Buscando avisos {}", id);
        
        Response<AvisosDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Avisos> user = avisosService.findById(id);
        if (!user.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(avisosConverter.entityToDTO(user.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Response<AvisosDTO>> deleteNotifications(@PathVariable("id") Integer id) {
        log.info("Buscando avisos {}", id);
        
        Response<AvisosDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Avisos> not = avisosService.findById(id);
        if (!not.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.status(404).body(response);
        }

        try {avisosService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Notificação não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(avisosConverter.entityToDTO(not.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<AvisosDTO>>> getAllEvents() {
        
        Response<List<AvisosDTO>> response = new Response<>();

        Optional<List<Avisos>> user = avisosService.findAll();
        if (user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<AvisosDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(avisosConverter.entityToDTO(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
}
