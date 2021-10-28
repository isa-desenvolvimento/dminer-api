package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Notification;
import com.dminer.repository.NotificationRepository;
import com.dminer.services.interfaces.INotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements INotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public Notification persist(Notification notification) {
        log.info("Persistindo notificação: {}", notification);
        return notificationRepository.save(notification);
    }

    @Override
    public Optional<Notification> findById(int id) {
        log.info("Buscando uma notificação pelo id {}", id);
		return notificationRepository.findById(id);
    }

    @Override
    public Optional<List<Notification>> findAll() {
        log.info("Buscando todas as notificações");
		return Optional.ofNullable(notificationRepository.findAll());
    }

    @Override
    public void delete(int id) {
        log.info("Excluindo uma notificação pelo id {}", id);
		notificationRepository.deleteById(id);		        
    }
    
}
