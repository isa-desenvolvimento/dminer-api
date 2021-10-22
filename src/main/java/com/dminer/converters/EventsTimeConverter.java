package com.dminer.converters;

import com.dminer.dto.EventsDTO;
import com.dminer.dto.EventsRequestDTO;
import com.dminer.entities.Events;
import com.dminer.enums.EventsTime;
import com.dminer.utils.UtilDataHora;

import org.springframework.stereotype.Service;

@Service
public class EventsTimeConverter {
    
    public Events dtoToEntity(EventsDTO dto) {
        Events e = new Events();
        e.setId(dto.getId());
        e.setAllDay(dto.getAllDay());
        e.setDescription(dto.getDescription());
        e.setEndDate(UtilDataHora.stringToDate(dto.getEndDate()));
        e.setStartDate(UtilDataHora.stringToDate(dto.getStartDate()));
        e.setEndRepeat(EventsTime.valueOf(dto.getEndRepeat()));
        e.setStartRepeat(EventsTime.valueOf(dto.getStartRepeat()));
        e.setTitle(dto.getTitle());
        return e;
    }

    public Events requestDtoToEntity(EventsRequestDTO dto) {
        Events e = new Events();        
        e.setAllDay(dto.getAllDay());
        e.setDescription(dto.getDescription());
        e.setEndDate(UtilDataHora.stringToDate(dto.getEndDate()));
        e.setStartDate(UtilDataHora.stringToDate(dto.getStartDate()));
        e.setEndRepeat(EventsTime.valueOf(dto.getEndRepeat()));
        e.setStartRepeat(EventsTime.valueOf(dto.getStartRepeat()));
        e.setTitle(dto.getTitle());
        return e;
    }

    public EventsDTO entityToDto(Events e) {
        EventsDTO dto = new EventsDTO();
        dto.setAllDay(e.getAllDay());
        dto.setDescription(e.getDescription());
        dto.setEndDate(UtilDataHora.dateToString(e.getEndDate()));
        dto.setStartDate(UtilDataHora.dateToString(e.getStartDate()));
        dto.setEndRepeat(e.getEndRepeat().getEventTime());
        dto.setStartRepeat(e.getStartRepeat().getEventTime());
        dto.setTitle(e.getTitle());
        return dto;
    }
}
