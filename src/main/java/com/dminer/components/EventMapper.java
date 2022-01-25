package com.dminer.components;

import lombok.AllArgsConstructor;

import com.dminer.dto.EventsDTO;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Component
@AllArgsConstructor
public class EventMapper {

    private static final Logger log = LoggerFactory.getLogger(EventMapper.class);

    public SseEmitter.SseEventBuilder toSseEventBuilder(EventsDTO event) {
        log.info("Disparando evento: {}", event);
        return SseEmitter.event()
                .id(RandomStringUtils.randomAlphanumeric(12))
                .name(event.getTitle())
                .data(event.getDescription());
    }
}