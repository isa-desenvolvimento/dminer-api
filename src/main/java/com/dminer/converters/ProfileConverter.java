package com.dminer.converters;

import com.dminer.dto.ProfileDTO;
import com.dminer.dto.ProfileRequestDTO;
import com.dminer.entities.Profile;

import org.springframework.stereotype.Service;

@Service
public class ProfileConverter {

    public ProfileDTO entityToDTO(Profile entity) {
        ProfileDTO dto = new ProfileDTO();
        if (entity == null) return dto;
        dto.setId(entity.getId());
        dto.setTitle(entity.getDescrible() != null ? entity.getDescrible() : "");
        return dto;
    }

    public Profile dtoToEntity(ProfileDTO dto) {
        Profile c = new Profile();
        if (dto == null) return c;
        c.setId(dto.getId());
        c.setDescrible(dto.getTitle() != null ? dto.getTitle() : "");
        return c;
    }

    public Profile requestDtoToEntity(ProfileRequestDTO dto) {
        Profile c = new Profile();
        if (dto == null) return c;
        c.setDescrible(dto.getTitle() != null ? dto.getTitle() : "");
        return c;
    }
}
