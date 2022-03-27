package com.dminer.sse;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseEmitterEvents {

    SseEmitter emitter(String json);
}