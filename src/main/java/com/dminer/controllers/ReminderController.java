package com.dminer.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.ReminderConverter;
import com.dminer.dto.ReminderDTO;
import com.dminer.dto.ReminderRequestDTO;
import com.dminer.entities.Reminder;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.ReminderService;
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
@RequestMapping("/reminder")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReminderController {
    
    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private ReminderService reminderService;

    @Autowired 
    private ReminderConverter reminderConverter;

    @Autowired
    private UserService userService;

    
    private void validateRequestDto(ReminderRequestDTO reminderRequestDTO, BindingResult result) {
        if (reminderRequestDTO.getIdUser() == null) {
            result.addError(new ObjectError("ReminderRequestDTO", "Id do usuário precisa estar preenchido."));
		} else {
            Optional<User> findById = userService.findById(reminderRequestDTO.getIdUser());
            if (!findById.isPresent()) {
                result.addError(new ObjectError("ReminderRequestDTO", "Usuário não encontrado."));
            }
        }

        if (reminderRequestDTO.getReminderDescrible() == null || reminderRequestDTO.getReminderDescrible().isEmpty()) {
            result.addError(new ObjectError("ReminderRequestDTO", "Descrição do lembrete precisa estar preenchido."));			
		}

        if (reminderRequestDTO.getDataHora() == null || reminderRequestDTO.getDataHora().isEmpty()) {
            result.addError(new ObjectError("ReminderRequestDTO", "Data do lembrete precisa estar preenchido."));
		} else {
            try {
                Timestamp.valueOf(reminderRequestDTO.getDataHora());
            } catch (IllegalArgumentException e) {
                result.addError(new ObjectError("ReminderRequestDTO", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss."));
            }
        }
    }


    @PostMapping
    public ResponseEntity<Response<ReminderDTO>> create(@Valid @RequestBody ReminderRequestDTO notificationRequest, BindingResult result) {
    
        log.info("Salvando uma nova notificação {}", notificationRequest);

		Response<ReminderDTO> response = new Response<>();
        validateRequestDto(notificationRequest, result);
        if (result.hasErrors()) {
            log.info("Erro validando notificationRequest: {}", notificationRequest);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Reminder reminder = reminderService.persist(reminderConverter.requestDtoToEntity(notificationRequest));
        response.setData(reminderConverter.entityToDto(reminder));

        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<ReminderDTO>> getNotifications(@PathVariable("id") Integer id) {
        log.info("Buscando notificação {}", id);
        
        Response<ReminderDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Reminder> remi = reminderService.findById(id);
        if (!remi.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(reminderConverter.entityToDto(remi.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Response<ReminderDTO>> deleteNotifications(@PathVariable("id") Integer id) {
        log.info("Buscando notificação {}", id);
        
        Response<ReminderDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Reminder> not = reminderService.findById(id);
        if (!not.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.status(404).body(response);
        }

        try {reminderService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Notificação não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(reminderConverter.entityToDto(not.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<ReminderDTO>>> getAllEvents() {
        
        Response<List<ReminderDTO>> response = new Response<>();

        Optional<List<Reminder>> remi = reminderService.findAll();
        if (remi.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<ReminderDTO> eventos = new ArrayList<>();
        remi.get().forEach(u -> {
            eventos.add(reminderConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
}