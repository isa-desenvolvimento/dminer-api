package com.dminer.components;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.dminer.dto.EventsDTO;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@Component
@Slf4j
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(EventsDTO event) {
        return SseEmitter.event()
                .id(RandomStringUtils.randomAlphanumeric(12))
                .name(event.getTitle())
                .data(event.getDescription());
    }
}