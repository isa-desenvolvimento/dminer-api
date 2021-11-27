package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.ReminderDTO;
import com.dminer.dto.ReminderRequestDTO;
import com.dminer.entities.Reminder;
import com.dminer.entities.User;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReminderConverter {
    
    @Autowired
    private UserService userService;

    public Reminder dtoToEntity(ReminderDTO dto) {
        Reminder reminder = new Reminder();
        reminder.setId(dto.getId());
        reminder.setReminderDescrible(dto.getReminder());
        reminder.setDataHora(UtilDataHora.toTimestamp(dto.getDataHora()));
        Optional<User> findById = userService.findById(dto.getIdUser());
        if (findById.isPresent()) {
            reminder.setUser(findById.get());
        }
        reminder.setActive( dto.getActive() == null ? true : dto.getActive() );
        return reminder;
    }

    public Reminder requestDtoToEntity(ReminderRequestDTO dto) {
        Reminder reminder = new Reminder();
        reminder.setReminderDescrible(dto.getReminder());
        reminder.setDataHora(UtilDataHora.toTimestamp(dto.getDataHora()));
        Optional<User> findById = userService.findById(dto.getIdUser());
        if (findById.isPresent()) {
            reminder.setUser(findById.get());
        }
        reminder.setActive(true);
        return reminder;
    }

    public ReminderDTO entityToDto(Reminder reminder) {
        ReminderDTO notificationDTO = new ReminderDTO();
        notificationDTO.setId(reminder.getId());
        notificationDTO.setIdUser(reminder.getUser().getId());
        notificationDTO.setReminder(reminder.getReminderDescrible());
        notificationDTO.setDataHora(UtilDataHora.timestampToString(reminder.getDataHora()));
        notificationDTO.setActive(reminder.getActive());
        return notificationDTO;
    }

}
