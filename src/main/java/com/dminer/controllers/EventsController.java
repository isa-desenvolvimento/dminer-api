package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.EventsTimeConverter;
import com.dminer.dto.EventsDTO;
import com.dminer.dto.EventsRequestDTO;
import com.dminer.entities.Events;
import com.dminer.enums.EventsTime;
import com.dminer.response.Response;
import com.dminer.services.EventsService;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class EventsController {
    
    private static final Logger log = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    private EventsService eventService;

    @Autowired
    private EventsTimeConverter eventsTimeConverter;

    @Autowired
    private Environment env;


    private void validateRequestDto(EventsRequestDTO eventsRequestDTO, BindingResult result) {
        if (eventsRequestDTO.getTitle() == null) {
            result.addError(new ObjectError("eventsRequestDTO", "Titulo precisa estar preenchido."));			
		}

        if (eventsRequestDTO.getStartDate() == null) {
            result.addError(new ObjectError("eventsRequestDTO", "Data de inicio precisa estar preenchido."));
		}

        if (eventsRequestDTO.getAllDay() == null) {
			eventsRequestDTO.setAllDay(false);
		}

        if (eventsRequestDTO.getStartRepeat() == null) {
			eventsRequestDTO.setStartRepeat(EventsTime.NO_REPEAT.name());
		}

        if (eventsRequestDTO.getEndRepeat() == null) {
			eventsRequestDTO.setEndRepeat(EventsTime.NO_REPEAT.name());
		}

        if (eventsRequestDTO.getReminder() == null) {
			eventsRequestDTO.setReminder(EventsTime.NO_REMINDER.name());
		}
    }

    @PostMapping
    public ResponseEntity<Response<EventsDTO>> create(@Valid @RequestBody EventsRequestDTO eventsRequestDTO, BindingResult result) {
        
		log.info("Salvando um novo evento {}", eventsRequestDTO);

		Response<EventsDTO> response = new Response<>();
        validateRequestDto(eventsRequestDTO, result);
        if (result.hasErrors()) {
            log.info("Erro validando eventsRequestDTO: {}", eventsRequestDTO);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Events events = eventService.persist(eventsTimeConverter.requestDtoToEntity(eventsRequestDTO));
        response.setData(eventsTimeConverter.entityToDto(events));

        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<EventsDTO>> getEvents(@PathVariable("id") Integer id) {
        log.info("Buscando evento {}", id);
        
        Response<EventsDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Events> user = eventService.findById(id);
        if (!user.isPresent()) {
            response.getErrors().add("Evento não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(eventsTimeConverter.entityToDto(user.get()));
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> deleteEvent(@PathVariable("id") Integer id) {
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {eventService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Evento não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/fetchEvents")
    public ResponseEntity<Response<List<EventsDTO>>> getAllEvents() {
        
        Response<List<EventsDTO>> response = new Response<>();

        Optional<List<Events>> user = eventService.findAll();
        if (user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<EventsDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(eventsTimeConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/fetchEventsByYear")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByYear(@RequestParam("year") String year) {
        
        Response<List<EventsDTO>> response = new Response<>();

        Optional<List<Events>> user = Optional.empty();
        if (isProd()) {
            user = eventService.fetchEventsByYear(year);
        } else {
            user = eventService.fetchEventsByYearSqlServer(year);
        }

        if (user.isPresent() && user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<EventsDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(eventsTimeConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/fetchEventsByMonth")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByMonth(@RequestParam String year, @RequestParam String month) {
        Response<List<EventsDTO>> response = new Response<>();

        Optional<List<Events>> user = Optional.empty();
        if (isProd()) {
            user = eventService.fetchEventsByMonth(year, month);
        } else {
            user = eventService.fetchEventsByMonthSqlServer(year, month);
        }

        if (user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<EventsDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(eventsTimeConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/fetchEventsByDate")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByDate(@RequestParam("date") String date) {
        
        Response<List<EventsDTO>> response = new Response<>();
        
        Optional<List<Events>> user = Optional.empty();
        if (isProd()) {
            user = eventService.fetchEventsByDate(date);
        } else {
            user = eventService.fetchEventsByDate2(date);
        }

        if (user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<EventsDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(eventsTimeConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/fetchEventsInBetween")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsInBetween(@RequestParam("dtInicio") String dtInicio, @RequestParam("dtFim") String dtFim) {
        
        Response<List<EventsDTO>> response = new Response<>();

        Optional<List<Events>> user = Optional.empty();
        if (isProd()) {
            user = eventService.fetchEventsInBetween(dtInicio, dtFim);
        } else {
            user = eventService.fetchEventsInBetween2(dtInicio, dtFim);
        }

        if (user.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.status(404).body(response);
        }
        
        List<EventsDTO> eventos = new ArrayList<>();
        user.get().forEach(u -> {
            eventos.add(eventsTimeConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }

    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
