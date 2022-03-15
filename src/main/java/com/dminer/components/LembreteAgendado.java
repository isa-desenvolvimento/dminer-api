package com.dminer.components;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.dminer.controllers.ServerSendEvents;
import com.dminer.entities.Reminder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LembreteAgendado {
    
    private static final Logger log = LoggerFactory.getLogger(LembreteAgendado.class);

    @Autowired
    private ServerSendEvents serverSendEvents;

    public LembreteAgendado() { }
    
    public void execute(Reminder reminder) {
        log.info("Agendando o lembrete: " + reminder.toString());
        Date date = new Date(reminder.getDate().getTime());
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override public void run() {
                serverSendEvents.setReminder(reminder);
                serverSendEvents.streamSseReminder();
                reminder.desactivate();
            }
        }, date);
    }
}