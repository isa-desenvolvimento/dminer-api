package com.dminer.controllers;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import com.dminer.components.LembreteAgendado;
import com.dminer.dto.ReminderRequestDTO;
import com.dminer.entities.Reminder;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.ReminderService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cron")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class Cron {
    

    @Autowired
    private LembreteAgendado lembreteAgendado;

    // @Autowired
    // private ServerSendEvents serverSendEvents;

    @Autowired
    private UserService userService;

    @Autowired
    private ReminderService reminderService;


    @PostMapping("/reminder")
    public ResponseEntity<Response<Reminder>> create(@RequestBody ReminderRequestDTO dto) {
        Response<Reminder> response = new Response<>();
        
        Timestamp time = UtilDataHora.toTimestamp(dto.getDate());
        Timestamp agora = new Timestamp(Date.from(Instant.now()).getTime());
        
        if (time.getTime() < agora.getTime()) {
            response.getErrors().add("Data precisa ser superior a data atual");
            response.getErrors().add("Data informada: " + dto.getDate() + "\t Data atual: " + UtilDataHora.dateToStringUTC(agora));
        }
        
        Optional<User> opt = userService.findByLogin(dto.getLogin());
        if (opt.isPresent()) {
            Reminder reminder = new Reminder();
            reminder.setDate(time);
            reminder.setReminderDescrible(dto.getReminder());
            reminder.setUser(opt.get());
            reminder.setActive(true);
            reminder = reminderService.persist(reminder);

            lembreteAgendado.execute(reminder);
            response.setData(reminder);            
            return ResponseEntity.ok().body(response);
        } 

        response.getErrors().add("Usuário não encontrado");
        return ResponseEntity.badRequest().body(response);
    }

}
