package com.dminer.services.interfaces;

import com.dminer.dto.EventsDTO;

public interface NotificationService {
    
    void sendNotification(String memberId, EventsDTO event);
}
