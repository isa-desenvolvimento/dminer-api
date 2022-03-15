package com.dminer.converters;

import com.dminer.dto.PriorityRequestDTO;
import com.dminer.dto.PriorityDTO;
import com.dminer.entities.Priority;

import org.springframework.stereotype.Service;

@Service
public class PriorityConverter {

    public PriorityDTO entityToDTO(Priority entity) {
        PriorityDTO dto = new PriorityDTO();
        if (entity == null) return dto;
        dto.setId(entity.getId());
        dto.setName(entity.getName() != null ? entity.getName() : "");
        return dto;
    }

    public Priority dtoToEntity(PriorityDTO dto) {
        Priority c = new Priority();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setName(dto.getName() != null ? dto.getName() : "");
        return c;
    }

    public Priority requestDtoToEntity(PriorityRequestDTO dto) {
        Priority c = new Priority();
        if (dto == null) return c;
        c.setName(dto.getName() != null ? dto.getName() : "");
        return c;
    }
}
