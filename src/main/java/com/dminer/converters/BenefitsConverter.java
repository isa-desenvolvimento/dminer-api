package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.BenefitsDTO;
import com.dminer.dto.BenefitsRequestDTO;
import com.dminer.entities.Benefits;
import com.dminer.entities.Permission;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BenefitsConverter {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionRepository permissionRepository;


    public BenefitsDTO entityToDTO(Benefits entity) {
        BenefitsDTO dto = new BenefitsDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent() != null ? entity.getContent() : "");
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : "");
        dto.setDate(entity.getDate() != null ? UtilDataHora.timestampToString(entity.getDate()) : null);        
        dto.setCreator(entity.getCreator().getLogin());
        dto.setImage(entity.getImage());
        if (entity.getPermission() != null)
            dto.setPermission(entity.getPermission().getId());
        return dto;
    }

    public Benefits dtoToEntity(BenefitsDTO dto) {
        Benefits c = new Benefits();
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        Optional<User> user = userService.findByLogin(dto.getCreator());
        if (user.isPresent())
            c.setCreator(user.get());
        Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
        if (findById.isPresent())
            c.setPermission(findById.get());
        c.setImage(dto.getImage());
        return c;
    }

    public Benefits requestDtoToEntity(BenefitsRequestDTO dto) {
        Benefits c = new Benefits();
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        Optional<User> user = userService.findByLogin(dto.getCreator());
        if (user.isPresent())
            c.setCreator(user.get());
        Optional<Permission> findById = permissionRepository.findById(dto.getPermission());
        if (findById.isPresent())
            c.setPermission(findById.get());
        c.setImage(dto.getImage());
        return c;
    }

}
