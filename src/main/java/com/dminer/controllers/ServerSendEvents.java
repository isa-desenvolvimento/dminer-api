package com.dminer.controllers;

import org.springframework.web.bind.annotation.GetMapping;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dminer.components.LembreteAgendado;
import com.dminer.dto.EventsDTO;
import com.dminer.entities.Notification;
import com.dminer.entities.Reminder;
import com.dminer.services.EmitterService;

import com.dminer.services.UserService;
import com.dminer.services.interfaces.NotificationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/server-send-events")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ServerSendEvents {
    
    private static final Logger log = LoggerFactory.getLogger(ServerSendEvents.class);

    private Reminder reminder;

    private Notification notification;

    private SseEmitter emitter;


    @GetMapping("/reminder")
    public  SseEmitter streamSseReminder() {
        log.info("Disparando o reminder no endpoint");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                emitter = new SseEmitter();
                emitter.send(reminder.toJson());
                emitter.complete();
                Thread.sleep(3000);
            }catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        executor.shutdown();
        return emitter;
    }


    @GetMapping("/notification")
    public  SseEmitter streamSseNotification() {
        log.info("Disparando evento sse de notificação");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                emitter = new SseEmitter();
                emitter.send(notification.toJson());
                emitter.complete();
                Thread.sleep(3000);
            }catch (Exception ex) {
                emitter.completeWithError(ex);
            } 
        });
        executor.shutdown();
        return emitter;
    }


    @GetMapping("/birthday")
    public  SseEmitter streamSseBirthday() {        
        
        String json = "disparando evento sse de aniversário";

        System.out.println(json);
        SseEmitter emitter = new SseEmitter(); 
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                emitter.send(json);
                emitter.complete();
            }catch (Exception ex) {
                emitter.completeWithError(ex);
            } 
        });
        executor.shutdown();
        return emitter;
    }



    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }


}
