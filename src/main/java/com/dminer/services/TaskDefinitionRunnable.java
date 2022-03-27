package com.dminer.services;

import com.dminer.sse.SseEmitterEvents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskDefinitionRunnable implements Runnable {

    @Autowired
    private SseEmitterEvents sendEvents;

    private String json;

    @Override
    public void run() {
        sendEvents.emitter(json);
    }


    public void setSseEmitterEvent(SseEmitterEvents sendEvents, String json) {
        this.sendEvents = sendEvents;
        this.json = json;
    }
}
