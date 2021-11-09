package com.dminer.converters;

import com.dminer.dto.PermissionDTO;
import com.dminer.dto.PermissionRequestDTO;
import com.dminer.entities.Permission;

import org.springframework.stereotype.Service;

@Service
public class PermissionConverter {

    public PermissionDTO entityToDTO(Permission entity) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(entity.getId());
        dto.setPermission(entity.getPermission() != null ? entity.getPermission() : "");
        return dto;
    }

    public Permission dtoToEntity(PermissionDTO dto) {
        Permission c = new Permission();
        c.setId(dto.getId());
        c.setPermission(dto.getPermission() != null ? dto.getPermission() : "");
        return c;
    }

    public Permission requestDtoToEntity(PermissionRequestDTO dto) {
        Permission c = new Permission();
        c.setPermission(dto.getPermission() != null ? dto.getPermission() : "");
        return c;
    }
}
