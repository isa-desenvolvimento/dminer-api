package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dminer.converters.EventsTimeConverter;
import com.dminer.dto.EventsDTO;
import com.dminer.dto.EventsRequestDTO;
import com.dminer.entities.Events;
import com.dminer.response.Response;
import com.dminer.services.EventsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping
    public ResponseEntity<Response<EventsDTO>> create(@RequestBody EventsRequestDTO eventsRequestDTO) {
        
		log.info("Salvando um novo evento {}", eventsRequestDTO);

		Response<EventsDTO> response = new Response<>();
        // perguntar Andressa quais campos são obrigatórios
        if (eventsRequestDTO.getTitle() == null) {
			response.getErrors().add("Descrição precisa estar preenchido.");            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @GetMapping()
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
}
