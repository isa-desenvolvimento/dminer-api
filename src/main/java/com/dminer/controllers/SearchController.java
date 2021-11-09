package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.dminer.entities.Events;
import com.dminer.entities.Notification;
import com.dminer.entities.Reminder;
import com.dminer.response.Response;
import com.dminer.services.EventsService;
import com.dminer.services.NotificationService;
import com.dminer.services.ReminderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class SearchController {
    

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private EventsService eventsService;

    @Autowired
    private Environment env;


    @GetMapping(value = "/{keyword}")
    public ResponseEntity<Response<List<Object>>> getAllEvents(@PathVariable String keyword) {
        
        Response<List<Object>> response = new Response<>();
        
        List<Object> dados = new ArrayList<>();
        Optional<List<Notification>> searchNotification = notificationService.search(keyword);
        if (! searchNotification.get().isEmpty()) {
            searchNotification.get().forEach(u -> {
                dados.add(u);
            });
        }

        Optional<List<Reminder>> searchReminder = reminderService.search(keyword);
        if (! searchReminder.get().isEmpty()) {
            searchReminder.get().forEach(u -> {
                dados.add(u);
            });
        }

        if (isProd()) {
            Optional<List<Events>> searchEvents = eventsService.searchPostgres(keyword);
            if (! searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    dados.add(u);
                });
            }
        } else {
            Optional<List<Events>> searchEvents = eventsService.search(keyword);
            if (! searchEvents.get().isEmpty()) {
                searchEvents.get().forEach(u -> {
                    dados.add(u);
                });
            }
        }

        response.setData(dados);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}