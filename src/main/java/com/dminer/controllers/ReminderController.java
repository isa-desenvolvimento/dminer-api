package com.dminer.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dminer.constantes.MessagesConst;
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
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Autowired
    private Environment env;
    
    

    @PostMapping("/{login}")
    public ResponseEntity<Response<ReminderDTO>> create(@PathVariable("login") String login, @Valid @RequestBody ReminderRequestDTO reminderRequest, BindingResult result) {
    
        log.info("Salvando um novo lembrete {}", reminderRequest);

		Response<ReminderDTO> response = new Response<>();

        validateRequestDto(reminderRequest, result);
        if (result.hasErrors()) {
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }
        
        Reminder reminder = reminderService.persist(reminderConverter.requestDtoToEntity(reminderRequest));
        response.setData(reminderConverter.entityToDto(reminder));

        return ResponseEntity.ok().body(response);
    }


    @PutMapping("/{login}")
    public ResponseEntity<Response<ReminderDTO>> update(@PathVariable("login") String login, @Valid @RequestBody ReminderDTO reminderRequest, BindingResult result) {
    
        log.info(MessagesConst.ALTERANDO_REGISTRO, reminderRequest);

		Response<ReminderDTO> response = new Response<>();
		validateDto(reminderRequest, result);
        if (result.hasErrors()) {
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }
        
        Reminder reminder = reminderService.persist(reminderConverter.dtoToEntity(reminderRequest));
        response.setData(reminderConverter.entityToDto(reminder));
        return ResponseEntity.ok().body(response);
    }
    
    
    @GetMapping(value = "/{login}/find/{id}")
    public ResponseEntity<Response<ReminderDTO>> get(@PathVariable("login") String login, @PathVariable("id") Integer id) {
        log.info("Buscando lembrete {} {}", login, id);
        
        Response<ReminderDTO> response = new Response<>();
        if (id == null) {
            response.addError(MessagesConst.INFORME_ID);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Reminder> remi = reminderService.findById(id);
        if (!remi.isPresent()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        response.setData(reminderConverter.entityToDto(remi.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{login}/{id}")
    public ResponseEntity<Response<ReminderDTO>> delete(@PathVariable("login") String login, @PathVariable("id") Integer id) {
        log.info("Buscando lembrete {}", id);
        
        Response<ReminderDTO> response = new Response<>();
        if (id == null) {
            response.addError(MessagesConst.INFORME_ID);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Reminder> not = reminderService.findById(id);
        if (!not.isPresent()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        try {reminderService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        response.setData(reminderConverter.entityToDto(not.get()));
        return ResponseEntity.ok().body(response);
    }


    
    @GetMapping("/{login}/all")
    public ResponseEntity<Response<List<ReminderDTO>>> getAll(@PathVariable("login") String login) {
        
        Response<List<ReminderDTO>> response = new Response<>();

        Optional<List<Reminder>> remi = reminderService.findAll();
        if (remi.get().isEmpty()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }
        List<Reminder> reminder = remi.get();
                
        List<ReminderDTO> eventos = new ArrayList<>();
        reminder.forEach(u -> {
            if (u.getUser().getLogin().equals(login))
                eventos.add(reminderConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/search/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<List<ReminderDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<List<ReminderDTO>> response = new Response<>();
        
        List<Reminder> search = reminderService.search(keyword, login, isProd());
        search.forEach(reminder -> {
            ReminderDTO dto = reminderConverter.entityToDto(reminder);
            response.getData().add(dto); 
        });
        
        return ResponseEntity.ok().body(response);
    }

    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }


    private void validateRequestDto(ReminderRequestDTO reminderRequestDTO, BindingResult result) {
        if (reminderRequestDTO.getLogin() == null) {
            result.addError(new ObjectError("ReminderRequestDTO", "Id do usuário precisa estar preenchido."));
		} else {
            Optional<User> findById = userService.findByLogin(reminderRequestDTO.getLogin());
            if (!findById.isPresent()) {
                result.addError(new ObjectError("ReminderRequestDTO", "Usuário não encontrado."));
            }
        }

        if (reminderRequestDTO.getReminder() == null || reminderRequestDTO.getReminder().isEmpty()) {
            result.addError(new ObjectError("ReminderRequestDTO", "Descrição do lembrete precisa estar preenchido."));			
		}

        if (reminderRequestDTO.getDate() == null || reminderRequestDTO.getDate().isEmpty()) {
            result.addError(new ObjectError("ReminderRequestDTO", "Data do lembrete precisa estar preenchido."));
		} else {
            try {
                Timestamp.valueOf(reminderRequestDTO.getDate());
            } catch (IllegalArgumentException e) {
                result.addError(new ObjectError("ReminderRequestDTO", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss."));
            }
        }        
    }

    
    private void validateDto(ReminderDTO dto, BindingResult result) {
    	
    	if (dto.getId() == null) {
    		result.addError(new ObjectError("dto", "Id do lembrete precisa estar preenchido."));
    	} else {
    		Optional<Reminder> findById = reminderService.findById(dto.getId());
            if (!findById.isPresent()) {
                result.addError(new ObjectError("dto", "Lembrete não encontrado."));
            }
    	}
    	
        if (dto.getLogin() == null) {
            result.addError(new ObjectError("dto", "Login do usuário precisa estar preenchido."));
		} else {
            Optional<User> findById = userService.findByLogin(dto.getLogin());
            if (findById == null || !findById.isPresent()) {
                result.addError(new ObjectError("dto", "Usuário não encontrado."));
            }
        }

        if (dto.getReminder() == null || dto.getReminder().isEmpty()) {
            result.addError(new ObjectError("dto", "Descrição do lembrete precisa estar preenchido."));			
		}

        if (dto.getDate() == null || dto.getDate().isEmpty()) {
            result.addError(new ObjectError("dto", "Data do lembrete precisa estar preenchido."));
		} else {
            try {
                Timestamp.valueOf(dto.getDate());
            } catch (IllegalArgumentException e) {
                result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss."));
            }
        }        
    }
}
