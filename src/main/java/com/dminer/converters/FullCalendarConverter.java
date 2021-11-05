package com.dminer.converters;

import com.dminer.dto.FullCalendarDTO;
import com.dminer.dto.FullCalendarRequestDTO;
import com.dminer.entities.FullCalendar;
import com.dminer.utils.UtilDataHora;

import org.springframework.stereotype.Service;

@Service
public class FullCalendarConverter {

    public FullCalendar dtoToEntity(FullCalendarDTO dto) {
        FullCalendar e = new FullCalendar();
        e.setId(dto.getId());
        e.setAllDay(dto.getAllDay());
        if (!dto.getEnd().contains("1970") || !dto.getEnd().contains("1969"))
            e.setEnd(UtilDataHora.toTimestamp(dto.getEnd()));
        
        e.setStart(UtilDataHora.toTimestamp(dto.getStart()));
        e.setTitle(dto.getTitle());
        return e;
    }

    public FullCalendar requestDtoToEntity(FullCalendarRequestDTO dto) {
        FullCalendar e = new FullCalendar();        
        e.setAllDay(dto.getAllDay());
        if (!dto.getEnd().contains("1970") || !dto.getEnd().contains("1969"))
            e.setEnd(UtilDataHora.toTimestamp(dto.getEnd()));

        e.setStart(UtilDataHora.toTimestamp(dto.getStart()));
        e.setTitle(dto.getTitle());
        return e;
    }

    public FullCalendarDTO entityToDto(FullCalendar e) {
        FullCalendarDTO dto = new FullCalendarDTO();
        dto.setId(e.getId());
        dto.setAllDay(e.getAllDay());
        if (e.getEnd() != null) {
            dto.setEnd(UtilDataHora.timestampToString(e.getEnd()));
            if (dto.getEnd().contains("1970") || dto.getEnd().contains("1969")) dto.setEnd(null);
        }
        
        if (e.getStart() != null)
            dto.setStart(UtilDataHora.timestampToString(e.getStart()));
        dto.setTitle(e.getTitle());
        return dto;
    }

}
