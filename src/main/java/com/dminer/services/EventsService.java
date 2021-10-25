package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Events;
import com.dminer.repository.EventsTimeRepository;
import com.dminer.services.interfaces.IEventsService;

import org.hibernate.cfg.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.query.Param;
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

    
    public Optional<List<Events>> fetchEventsByYear(String year) {
        log.info("Buscando todos os eventos por ano: {}", year);
        return Optional.ofNullable(eventsTimeRepository.fetchEventsByYear(year));
    }


    public Optional<List<Events>> fetchEventsByMonth(String year, String month) {
        log.info("Buscando todos os eventos por ano/mês: {} - {}", year, month);
        return Optional.ofNullable(eventsTimeRepository.fetchEventsByMonth(year, month));
    }


    public Optional<List<Events>> fetchEventsByDate(@Param("date") String date) {
        log.info("Buscando todos os eventos por date: {}", date);
        return Optional.ofNullable(eventsTimeRepository.fetchEventsByDate(date));
    }


    @Profile("dev")
    public Optional<List<Events>> fetchEventsInBetween(String dtInicio, String dtFim) {
        log.info("Buscando todos os eventos que estejam entre {} e {}", dtInicio, dtFim);
        return Optional.ofNullable(eventsTimeRepository.fetchEventsInBetween(dtInicio, dtFim));
    }

}
