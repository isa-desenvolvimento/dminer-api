package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.dminer.entities.Notification;
import com.dminer.response.Response;
import com.dminer.services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
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


    @GetMapping(value = "/{keyword}")
    public ResponseEntity<Response<List<Object>>> getAllEvents(@PathVariable String keyword) {
        
        Response<List<Object>> response = new Response<>();

        Optional<List<Notification>> search = notificationService.search(keyword);
        if (search.get().isEmpty()) {
            response.getErrors().add("Nenhum dado encontrado");
            return ResponseEntity.status(404).body(response);
        }

        List<Object> eventos = new ArrayList<>();
        search.get().forEach(u -> {
            eventos.add(u);
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
}
