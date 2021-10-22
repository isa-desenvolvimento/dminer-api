package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Events;
import com.dminer.repository.EventsTimeRepository;
import com.dminer.services.interfaces.IEventsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventsService implements IEventsService {

    @Autowired
    private EventsTimeRepository eventsTimeRepository;

    private static final Logger log = LoggerFactory.getLogger(EventsService.class);


    @Override
    public Events persist(Events events) {
        log.info("Persistindo um evento {}", events);
        return eventsTimeRepository.save(events);
    }

    
    @Override
    public Optional<Events> findById(int id) {
        log.info("Buscando um evento por id {}", id);
        return eventsTimeRepository.findById(id);
    }


    @Override
    public void delete(int id) {
        log.info("Deletando um evento pelo id {}", id);
        eventsTimeRepository.deleteById(id);
    }


    @Override
    public Optional<List<Events>> findAll() {
        log.info("Buscando todos os eventos");
        return Optional.ofNullable(eventsTimeRepository.findAll());
    }    

}
