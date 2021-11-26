package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.NotificationDTO;
import com.dminer.dto.NotificationRequestDTO;
import com.dminer.entities.Notification;
import com.dminer.entities.User;
import com.dminer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationConverter {
    
    @Autowired
    private UserService userService;

    public Notification dtoToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setNotification(dto.getNotification());
        Optional<User> findById = userService.findByLogin(dto.getIdUser());
        if (findById.isPresent()) {
            notification.setUser(findById.get());
        }
        return notification;
    }

    public Notification requestDtoToEntity(NotificationRequestDTO dto) {
        Notification notification = new Notification();
        notification.setNotification(dto.getNotification());
        Optional<User> findById = userService.findByLogin(dto.getIdUser());
        if (findById.isPresent()) {
            notification.setUser(findById.get());
        }
        return notification;
    }

    public NotificationDTO entityToDto(Notification notification) {
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notification.getId());
        notificationDTO.setIdUser(notification.getUser().getLogin());
        notificationDTO.setNotification(notification.getNotification());
        return notificationDTO;
    }
}
