package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Events;
import com.dminer.repository.EventsTimeRepository;
import com.dminer.repository.EventsTimeRepositoryPostgres;
import com.dminer.repository.EventsTimeRepositorySqlServer;
import com.dminer.services.interfaces.IEventsService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventsService implements IEventsService {

    @Autowired
    private EventsTimeRepository eventsTimeRepository;

    @Autowired
    private EventsTimeRepositorySqlServer eventsTimeRepositorySqlServe;
    
    @Autowired
    private EventsTimeRepositoryPostgres eventsTimeRepositoryPostgres;
    

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
        log.info("SqlServe - Buscando todos os eventos por ano: {}", year);
        return Optional.ofNullable(eventsTimeRepositorySqlServe.fetchEventsByYear(year + "-01-01 00:00:00", year + "-12-31 23:59:59"));
    }


    public Optional<List<Events>> fetchEventsByMonth(String year, String month) {
        log.info("SqlServe - Buscando todos os eventos por ano/mês: {} - {}", year, month);
        return Optional.ofNullable(eventsTimeRepositorySqlServe.fetchEventsByMonth(year, month));
    }


    public Optional<List<Events>> fetchEventsByDate(String date, String date2) {
        log.info("SqlServe - Buscando todos os eventos por date: {}", date);
        return Optional.ofNullable(eventsTimeRepositorySqlServe.fetchEventsByDate(date, date + " 23:59:59"));  
    }


    
    public Optional<List<Events>> fetchEventsInBetween(String dtInicio, String dtFim) {
        log.info("SqlServe - Buscando todos os eventos que estejam entre {} e {}", dtInicio, dtFim);        
        return Optional.ofNullable(eventsTimeRepositorySqlServe.fetchEventsInBetween(dtInicio, dtFim)); 
    }


    public Optional<List<Events>> fetchEventsByYearPostgres(String year) {
        log.info("Postgres - Buscando todos os eventos por ano: {}", year);
        return Optional.ofNullable(eventsTimeRepositoryPostgres.fetchEventsByYear(year + "-01-01 01:00:00", year + "-12-31 12:59:59"));
    }


    public Optional<List<Events>> fetchEventsByMonthPostgres(String year, String month) {
        log.info("Postgres - Buscando todos os eventos por ano/mês: {} - {}", year, month);
        return Optional.ofNullable(eventsTimeRepositoryPostgres.fetchEventsByMonth(year, month));
    }


    public Optional<List<Events>> fetchEventsByDatePostgres(String date, String date2) {
        log.info("Postgres - Buscando todos os eventos por date: {}", date);
        return Optional.ofNullable(eventsTimeRepositoryPostgres.fetchEventsByDate(date, date2));      
    }


    
    public Optional<List<Events>> fetchEventsInBetweenPostgres(String dtInicio, String dtFim) {
        log.info("Postgres - Buscando todos os eventos que estejam entre {} e {}", dtInicio, dtFim);        
        return Optional.ofNullable(eventsTimeRepositoryPostgres.fetchEventsInBetween(dtInicio, dtFim));         
    }


    public Optional<List<Events>> search(String keyword) {
        if (keyword != null) {
            return Optional.ofNullable(eventsTimeRepositorySqlServe.search(keyword));
        }
        return Optional.ofNullable(eventsTimeRepository.findAll());
    }

    public Optional<List<Events>> searchPostgres(String keyword) {
        if (keyword != null) {
            return Optional.ofNullable(eventsTimeRepositoryPostgres.search(keyword));
        }
        return Optional.ofNullable(eventsTimeRepository.findAll());
    }
}
