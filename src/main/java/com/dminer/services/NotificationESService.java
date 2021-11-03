package com.dminer.services;

import java.util.List;

import com.dminer.entities.elasticsearch.NotificationES;
import com.dminer.repository.elasticsearch.NotificationESRepository;
import com.dminer.services.interfaces.INotificationESService;

import org.springframework.beans.factory.annotation.Autowired;

public class NotificationESService implements INotificationESService {

    @Autowired
    private NotificationESRepository notificationESRepository;


    @Override
    public List<NotificationES> findByNotificationDescrible(String notificationDescrible) {
        return notificationESRepository.findByNotificationDescrible(notificationDescrible);
    }
    
}
