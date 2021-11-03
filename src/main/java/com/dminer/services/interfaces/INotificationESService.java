package com.dminer.services.interfaces;

import java.util.List;

import com.dminer.entities.elasticsearch.NotificationES;

public interface INotificationESService {
    
    List<NotificationES> findByNotificationDescrible(String notificationDescrible);

}
