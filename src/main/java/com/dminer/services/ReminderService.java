package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Reminder;
import com.dminer.repository.ReminderRepository;
import com.dminer.services.interfaces.IReminderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReminderService implements IReminderService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private ReminderRepository reminderRepository;


    @Override
    public Reminder persist(Reminder reminder) {
        log.info("Persistindo lembrete: {}", reminder);
        return reminderRepository.save(reminder);
    }

    @Override
    public Optional<Reminder> findById(int id) {
        log.info("Buscando uma lembrete pelo id {}", id);
		return reminderRepository.findById(id);
    }

    @Override
    public Optional<List<Reminder>> findAll() {
        log.info("Buscando todas as lembretes");
		return Optional.ofNullable(reminderRepository.findAll());
    }

    @Override
    public void delete(int id) {
        log.info("Excluindo uma lembrete pelo id {}", id);
		reminderRepository.deleteById(id);        
    }

    
    
}