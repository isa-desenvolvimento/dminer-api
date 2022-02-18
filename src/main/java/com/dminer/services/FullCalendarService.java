package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.FullCalendar;
import com.dminer.repository.FullCalendarRepository;
import com.dminer.services.interfaces.IFullCalendarService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FullCalendarService implements IFullCalendarService {

    private static final Logger log = LoggerFactory.getLogger(FullCalendarService.class);

    @Autowired
    private FullCalendarRepository fullCalendarRepository;


    @Override
    public FullCalendar persist(FullCalendar fullCalendar) {
        log.info("Persistindo calendário: {}", fullCalendar);
        return fullCalendarRepository.save(fullCalendar);
    }

    @Override
    public Optional<FullCalendar> findById(int id) {
        log.info("Buscando um calendário por id {}", id);
        return fullCalendarRepository.findById(id);
    }

    @Override
    public Optional<List<FullCalendar>> findAll() {
        log.info("Buscando todos os calendários");
		return Optional.ofNullable(fullCalendarRepository.findAll());
    }

    @Override
    public void delete(int id) {
        log.info("Deletando um calendário pelo id {}", id);
        fullCalendarRepository.deleteById(id);        
    }
    
    // public List<FullCalendar> findAllByUser(int idUser) {
    //     log.info("Buscando todos os calendários para o usuario: " + idUser);
	// 	return Optional.ofNullable(fullCalendarRepository.findAll());
    // }

}
