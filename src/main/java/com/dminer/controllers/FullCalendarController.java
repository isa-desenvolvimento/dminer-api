package com.dminer.controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dminer.converters.FullCalendarConverter;
import com.dminer.dto.FullCalendarDTO;
import com.dminer.dto.FullCalendarRequestDTO;
import com.dminer.entities.FullCalendar;
import com.dminer.entities.Notification;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.FullCalendarService;
import com.dminer.services.NotificationService;
import com.dminer.services.TaskDefinitionRunnable;
import com.dminer.services.TaskSchedulingService;
import com.dminer.services.UserService;
import com.dminer.sse.SseEmitterEventsCalendar;
import com.dminer.utils.UtilDataHora;

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
@RequestMapping("/full-calendar")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FullCalendarController {
    
    private static final Logger log = LoggerFactory.getLogger(FullCalendarController.class);
    private static final ZoneId saoPauloZoneId = ZoneId.of("America/Sao_Paulo");

    @Autowired
    private FullCalendarService fullCalendarService;

    @Autowired 
    private FullCalendarConverter fullCalendarConverter;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private SseEmitterEventsCalendar sseEmitterEventsCalendar;

    @Autowired
    private TaskSchedulingService taskSchedulingService;

    @Autowired
    private TaskDefinitionRunnable taskDefinitionRunnable;



    @PostMapping
    public ResponseEntity<Response<FullCalendarDTO>> create(@Valid @RequestBody FullCalendarRequestDTO fullCalendarRequestDTO, BindingResult result) {
    
		Response<FullCalendarDTO> response = new Response<>();
        validateRequestDto(fullCalendarRequestDTO, result);
        if (result.hasErrors()) {
            log.info("Erro validando fullCalendarRequestDTO: {}", fullCalendarRequestDTO);
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }
        
        FullCalendar events = fullCalendarService.persist(fullCalendarConverter.requestDtoToEntity(fullCalendarRequestDTO));
        FullCalendarDTO dto = fullCalendarConverter.entityToDto(events);
        response.setData(dto);

        if (! dto.getUsers().isEmpty()) {
            createUserCalendarEvent(fullCalendarRequestDTO);
            dto.getUsers().forEach(user -> {
                log.info("Criando notificação para o usuário: " + user + " a partir de um evento calendário: {}", events);
                createUserNotification(user, dto.getTitle());            
            });
        }

        notificationService.newNotificationFromCalendarEvent(events);
        generateTaskSchedule(events);

        return ResponseEntity.ok().body(response);
    }

    private void generateTaskSchedule(FullCalendar events) {
        String id = events.getId().toString();
        String message = "O evento: " + events.getTitle() + ", acontecerá em 1 hora";
        
        sseEmitterEventsCalendar.setCalendar(events);
        taskDefinitionRunnable.setSseEmitterEvent(sseEmitterEventsCalendar, message);
        
        taskSchedulingService.scheduleATask(
            id, taskDefinitionRunnable, events.getStart(), 59, saoPauloZoneId
        );
    }

    /**
     * Cria um evento de calendário nos usuários marcados 
     * @param dto
     */
    private void createUserCalendarEvent(FullCalendarRequestDTO dto) {
        
        List<String> users = dto.getUsers();
        dto.setUsers(new ArrayList<>());

        users.forEach(user -> {
            log.info("Criando evento calendário para o usuário: " + user + " a partir de um evento calendário: {}", dto);
            FullCalendarRequestDTO dtoTemp = dto;
            dtoTemp.setCreator(user);
            fullCalendarService.persist(fullCalendarConverter.requestDtoToEntity(dtoTemp));
        });
        
    }


    /**
     * Cria uma notificação para cada usuário marcado no evento de calendário
     * @param user
     * @param content
     */
    private void createUserNotification(String user, String content) {
        Notification notification = new Notification();
        notification.setCreateDate(Timestamp.from(Instant.now()));
        notification.setNotification("Novo evento calendário foi criado: " + content);
        Optional<User> userTemp = userService.findByLogin(user);
        if (userTemp.isPresent()) {
            notification.setUser(userTemp.get());
            notificationService.persist(notification);
        } else {
            log.info("Usuário {} não encontrado na base de dados local", user);
        }
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<FullCalendarDTO>> get(@PathVariable("id") Integer id) {
        
        Response<FullCalendarDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<FullCalendar> calendar = fullCalendarService.findById(id);
        if (!calendar.isPresent()) {
            response.getErrors().add("Calendário não encontrado");
            return ResponseEntity.ok().body(response);
        }

        response.setData(fullCalendarConverter.entityToDto(calendar.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<FullCalendarDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<FullCalendarDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<FullCalendar> calendar = fullCalendarService.findById(id);
        if (!calendar.isPresent()) {
            response.getErrors().add("Calendário não encontrado");
            return ResponseEntity.ok().body(response);
        }

        try {fullCalendarService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Calendário não encontrado");
            return ResponseEntity.ok().body(response);
        }

        response.setData(fullCalendarConverter.entityToDto(calendar.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<FullCalendarDTO>>> getAll() {
        
        Response<List<FullCalendarDTO>> response = new Response<>();

        Optional<List<FullCalendar>> calendar = fullCalendarService.findAll();
        if (calendar.get().isEmpty()) {
            response.getErrors().add("Calendários não encontrados");
            return ResponseEntity.ok().body(response);
        }

        List<FullCalendarDTO> calendarios = new ArrayList<>();
        calendar.get().forEach(event -> {
            calendarios.add(fullCalendarConverter.entityToDto(event));

            // se a data fim do evento for null ou maior que a data atual
            // pode adicionar a fila de disparos de eventos
            if (event.getEnd().toLocalDateTime().isAfter(LocalDateTime.now())) {
                if (! taskSchedulingService.exists(event.getId().toString())) {
                    generateTaskSchedule(event);
                }
            }
        });

        response.setData(calendarios);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all/{login}")
    public ResponseEntity<Response<List<FullCalendarDTO>>> getAll(@PathVariable("login") String login) {
        
        Response<List<FullCalendarDTO>> response = new Response<>();

        Optional<List<FullCalendar>> calendar = fullCalendarService.findAll();
        if (calendar.get().isEmpty()) {
            response.getErrors().add("Calendários não encontrados");
            return ResponseEntity.ok().body(response);
        }

        List<FullCalendar> calendarios = calendar.get();
        calendarios = calendarios.stream()
        .filter(cal -> cal.getCreator().equals(login))
        .collect(Collectors.toList());

        List<FullCalendarDTO> calendariosDto = new ArrayList<>();
        
        calendarios.forEach(event -> {
            calendariosDto.add(fullCalendarConverter.entityToDto(event));

            // se a data fim do evento for null ou maior que a data atual
            // pode adicionar a fila de disparos de eventos
            if (event.getEnd().toLocalDateTime().isAfter(LocalDateTime.now())) {
                if (! taskSchedulingService.exists(event.getId().toString())) {
                    generateTaskSchedule(event);
                }
            }

        });
        
        response.setData(calendariosDto);
        return ResponseEntity.ok().body(response);
    }



    private void validateRequestDto(FullCalendarRequestDTO fullCalendarRequestDTO, BindingResult result) {
        if (fullCalendarRequestDTO.getTitle() == null || fullCalendarRequestDTO.getTitle().isBlank()) {
            result.addError(new ObjectError("fullCalendarRequestDTO", "Title precisa estar preenchido."));			
		}

        if (fullCalendarRequestDTO.getStart() == null || fullCalendarRequestDTO.getStart().isBlank()) {
            result.addError(new ObjectError("fullCalendarRequestDTO", "Data de inicio precisa estar preenchido."));
		}

        if (fullCalendarRequestDTO.getEnd() == null || fullCalendarRequestDTO.getEnd().isBlank()) {
            result.addError(new ObjectError("fullCalendarRequestDTO", "Data de fim precisa estar preenchido."));
		}

        if (fullCalendarRequestDTO.getCreator() == null || fullCalendarRequestDTO.getCreator().isBlank()) {
            result.addError(new ObjectError("fullCalendarRequestDTO", "Usuário que está criando precisa estar preenchido."));
		}

        if (fullCalendarRequestDTO.getAllDay() == null) {
			fullCalendarRequestDTO.setAllDay(false);
		}

    }
}
