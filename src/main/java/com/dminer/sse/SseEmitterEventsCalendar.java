package com.dminer.sse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dminer.entities.FullCalendar;
import com.dminer.services.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseEmitterEventsCalendar implements SseEmitterEvents {
    
    private SseEmitter emitter;

    @Autowired 
    private NotificationService notificationService;

    private FullCalendar calendar;

    @Override
    public SseEmitter emitter(String json) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                System.out.println("SseEmitter de calendário: " + json);
                emitter = new SseEmitter();
                emitter.send(json);
                emitter.complete();
                if (calendar != null) {
                    notificationService.newNotificationFromCalendarEvent(calendar);
                }
                Thread.sleep(3000);
            }catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        });
        executor.shutdown();
        return emitter;
    }

    public void setCalendar(FullCalendar calendar) {
        this.calendar = calendar;
    }
}