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
    private List<Reminder> reminders = new ArrayList<>();
    
    @Autowired
    private LembreteAgendado lembreteAgendado;
    
    private SseEmitter emitter;


    @GetMapping("/reminder")
    public  SseEmitter streamSseReminder() {
        reminders.forEach(reminder -> {
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
        });
        return emitter;
    }


    @GetMapping("/notification")
    public  SseEmitter streamSseNotification() {
        
        String json = "disparando evento sse de notificação";

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


    public void addReminder(Reminder reminder) {
        this.reminders.add(reminder);
    }











    void print(SseEventBuilder emitter) {
        System.out.println(emitter.toString());
    }







    @GetMapping(path = "/stream-flux", produces = "text/event-stream")
    public Flux<String> streamEvents() {
        return Flux.interval(Duration.ofSeconds(1))
          .map(sequence -> ServerSentEvent.<String> builder()
            .id(String.valueOf(sequence))
              .event("periodic-event")
              .data("SSE - " + LocalTime.now().toString())
              .build().toString());
    }


    @GetMapping("/stream-sse-mvc")
    public SseEmitter streamSseMvc(String json) {
        
        System.out.println("disparando evento sse");
        SseEmitter emitter = new SseEmitter();
        ExecutorService sseMvcExecutor = Executors.newSingleThreadExecutor();
        sseMvcExecutor.execute(() -> {
            try {
                    System.out.println("executando evento sse");
                // for (int i = 0; true; i++) {
                    SseEventBuilder event = SseEmitter.event()
                    //.data("SSE MVC - " + LocalTime.now().toString())
                    .data(json)
                    .id(String.valueOf(1))
                    .name("sse event - mvc");
                    emitter.send(event);
                    Thread.sleep(1000);
                // }
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        return emitter;
    }

}
