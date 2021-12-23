package com.dminer.converters;

import com.dminer.dto.PermissionDTO;
import com.dminer.dto.PermissionRequestDTO;
import com.dminer.entities.Permission;

import org.springframework.stereotype.Service;

@Service
public class PermissionConverter {

    public PermissionDTO entityToDTO(Permission entity) {
        PermissionDTO dto = new PermissionDTO();
        if (entity == null) return dto;
        dto.setId(entity.getId());
        dto.setName(entity.getName() != null ? entity.getName() : "");
        return dto;
    }

    public Permission dtoToEntity(PermissionDTO dto) {
        Permission c = new Permission();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setName(dto.getName() != null ? dto.getName() : "");
        return c;
    }

    public Permission requestDtoToEntity(PermissionRequestDTO dto) {
        Permission c = new Permission();
        if (dto == null) return c;
        c.setName(dto.getName() != null ? dto.getName() : "");
        return c;
    }
}
