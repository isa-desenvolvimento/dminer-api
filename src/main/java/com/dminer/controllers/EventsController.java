package com.dminer.controllers;

import java.time.YearMonth;
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

    @GetMapping(value = "/find-id/{id}")
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

    @DeleteMapping(value = "/delete/{id}")
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


    @GetMapping(value = "/{year}")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByYear(@PathVariable String year) {
        
        log.info("Parametro recebido: {}", year);
        Response<List<EventsDTO>> response = new Response<>();
        validarEntradaDatas(year, "Informe um ano", response);        
        if (!response.getErrors().isEmpty()) {
            log.info("Erro validando getEventsByYear");            
            return ResponseEntity.badRequest().body(response);
        }

        Optional<List<Events>> user = Optional.empty();
        user = eventService.fetchEventsByYear(year);

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

    
    @GetMapping(value = "/{year}/{month}")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByMonth(@PathVariable("year") String year, @PathVariable("month") String month) {
        
        Response<List<EventsDTO>> response = new Response<>();
        validarEntradaDatas(year, month, "Informe um ano", "Informe um mês", response);
        if (!response.getErrors().isEmpty()) {
            log.info("Erro validando getEventsByMonth");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<List<Events>> user = Optional.empty();
        YearMonth ym = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        user = eventService.fetchEventsByMonth(
            year + "-" + month + "-01 00:00:00", 
            year + "-" + month + "-" + ym.lengthOfMonth() + " 23:59:59"
        );
        


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


    @GetMapping(value = "/{year}/{month}/{day}")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsByDate(@PathVariable("year") String year, @PathVariable("month") String month, @PathVariable("day") String day) {
        
        Response<List<EventsDTO>> response = new Response<>();
        validarEntradaDatas(year, month, day, "Informe um ano", "Informe um mês", "Informe um dia", response);
        
        if (!response.getErrors().isEmpty()) {
            log.info("Erro validando getEventsByMonth");
            return ResponseEntity.badRequest().body(response);
        }

        String date = year + "-" + month + "-" + day;
        Optional<List<Events>> user = Optional.empty();
        user = eventService.fetchEventsByDate(date);

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


    @GetMapping(value = "/{year1}/{month1}/{day1}/and/{year2}/{month2}/{day2}")
    public ResponseEntity<Response<List<EventsDTO>>> getEventsInBetween(
        @PathVariable("year1") String year1, @PathVariable("month1") String month1, @PathVariable("day1") String day1,
        @PathVariable("year2") String year2, @PathVariable("month2") String month2, @PathVariable("day2") String day2
    ) {

        Response<List<EventsDTO>> response = new Response<>();

        validarEntradaDatas(year1, month1, day1, "Informe um ano", "Informe um mês", "Informe um dia", response);
        validarEntradaDatas(year2, month2, day2, "Informe um ano", "Informe um mês", "Informe um dia", response);
        
        if (!response.getErrors().isEmpty()) {
            log.info("Erro validando getEventsInBetween");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<List<Events>> user = Optional.empty();
        String dtInicio = year1 + "-" + month1 + "-" + day1 + " 00:00:00";
        String dtFim = year2 + "-" + month2 + "-" + day2 + " 23:59:59";
        user = eventService.fetchEventsInBetween(dtInicio, dtFim);

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


    private void validarEntradaDatas(String v1, String msg, Response result) {
        if (v1 == null || v1.isEmpty()) {
            result.getErrors().add(msg);
        }
    }

    private void validarEntradaDatas(String v1, String v2, String msg1, String msg2, Response result) {
        validarEntradaDatas(v1, msg1, result);
        validarEntradaDatas(v2, msg2, result);
    }

    private void validarEntradaDatas(String v1, String v2, String v3, String msg1, String msg2, String msg3, Response result) {
        validarEntradaDatas(v1, msg1, result);
        validarEntradaDatas(v2, msg2, result);
        validarEntradaDatas(v3, msg3, result);
    }

    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
