package com.dminer.controllers;

import java.lang.annotation.Annotation;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.dminer.components.LembreteAgendado;
import com.dminer.entities.Reminder;
import com.dminer.response.Response;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/{date}")
    public ResponseEntity<Response<Reminder>> create(@PathVariable("date") String date) {
    
        System.out.println(date);
        Response<Reminder> response = new Response<>();
        Reminder reminder = new Reminder();
        Timestamp time = UtilDataHora.toTimestamp(date);
        reminder.setDataHora(time);
        reminder.setReminderDescrible("tentando lembrar aqui");
        response.setData(reminder);

        lembreteAgendado.execute(reminder);
        return ResponseEntity.ok().body(response);
    }


    public static void main2(String[] args) {
        String date = "2021-11-26 11:43:00.09";
        System.out.println(UtilDataHora.toTimestamp(date));
    }
}
