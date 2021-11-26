package com.dminer.components;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.dminer.controllers.ServerSendEvents;
import com.dminer.entities.Reminder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LembreteAgendado {
    
    @Autowired
    private ServerSendEvents serverSendEvents;

    public LembreteAgendado() { }
    
    public void execute(Reminder reminder) {
        Date date = new Date(reminder.getDataHora().getTime());
        Timer timer = new Timer();        
        timer.schedule(new TimerTask() {
            @Override public void run() {
                serverSendEvents.addReminder(reminder);
                serverSendEvents.streamSseReminder();
            }
        }, date);
    }

}
