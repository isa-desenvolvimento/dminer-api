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
        dto.setTitle(entity.getPermission() != null ? entity.getPermission() : "");
        return dto;
    }

    public Permission dtoToEntity(PermissionDTO dto) {
        Permission c = new Permission();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setPermission(dto.getTitle() != null ? dto.getTitle() : "");
        return c;
    }

    public Permission requestDtoToEntity(PermissionRequestDTO dto) {
        Permission c = new Permission();
        if (dto == null) return c;
        c.setPermission(dto.getTitle() != null ? dto.getTitle() : "");
        return c;
    }
}
