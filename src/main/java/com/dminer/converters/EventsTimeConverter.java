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
        if (!dto.getEndDate().equals("1970-01-01 00:00:00"))
            e.setEndDate(UtilDataHora.stringToDate(dto.getEndDate()));
        
        e.setStartDate(UtilDataHora.stringToDate(dto.getStartDate()));
        EventsTime repeat = contains(dto.getEndRepeat()) ? EventsTime.valueOf(dto.getEndRepeat()) : EventsTime.NO_REPEAT;
        e.setEndRepeat(repeat);
        repeat = contains(dto.getEndRepeat()) ? EventsTime.valueOf(dto.getEndRepeat()) : EventsTime.NO_REPEAT;
        e.setStartRepeat(repeat);
        EventsTime reminder = contains(dto.getReminder()) ? EventsTime.valueOf(dto.getReminder()) : EventsTime.NO_REMINDER;
        e.setReminder(reminder);
        e.setTitle(dto.getTitle());
        return e;
    }

    public Events requestDtoToEntity(EventsRequestDTO dto) {
        Events e = new Events();        
        e.setAllDay(dto.getAllDay());
        e.setDescription(dto.getDescription());
        if (!dto.getEndDate().equals("1970-01-01 00:00:00"))
            e.setEndDate(UtilDataHora.stringToDate(dto.getEndDate()));

        e.setStartDate(UtilDataHora.stringToDate(dto.getStartDate()));
        EventsTime repeat = contains(dto.getEndRepeat()) ? EventsTime.valueOf(dto.getEndRepeat()) : EventsTime.NO_REPEAT;
        e.setEndRepeat(repeat);
        repeat = contains(dto.getEndRepeat()) ? EventsTime.valueOf(dto.getEndRepeat()) : EventsTime.NO_REPEAT;
        e.setStartRepeat(repeat);
        EventsTime reminder = contains(dto.getReminder()) ? EventsTime.valueOf(dto.getReminder()) : EventsTime.NO_REMINDER;
        e.setReminder(reminder);
        e.setTitle(dto.getTitle());
        return e;
    }

    public EventsDTO entityToDto(Events e) {
        EventsDTO dto = new EventsDTO();
        dto.setId(e.getId());
        dto.setAllDay(e.getAllDay());
        dto.setDescription(e.getDescription());
        if (e.getEndDate() != null) {
            dto.setEndDate(UtilDataHora.dateToString(e.getEndDate()));
            if (dto.getEndDate().equals("01/01/1970")) dto.setEndDate(null);
        }
        
        if (e.getStartDate() != null)
            dto.setStartDate(UtilDataHora.dateToString(e.getStartDate()));
        dto.setEndRepeat(e.getEndRepeat().getEventTime());
        dto.setStartRepeat(e.getStartRepeat().getEventTime());
        dto.setLocation(e.getLocation());
        dto.setReminder(e.getReminder().getEventTime());
        dto.setTitle(e.getTitle());
        return dto;
    }

    private boolean contains(String event) {
        EventsTime[] events =  EventsTime.values();
        for (EventsTime eventsTime : events) {
            if (eventsTime.name().equals("event"))
                return true;
        }
        return false;
    }
}
