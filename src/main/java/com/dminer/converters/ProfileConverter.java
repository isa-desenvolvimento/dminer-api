package com.dminer.converters;

import com.dminer.dto.ProfileDTO;
import com.dminer.dto.ProfileRequestDTO;
import com.dminer.entities.Profile;

import org.springframework.stereotype.Service;

@Service
public class ProfileConverter {

    public ProfileDTO entityToDTO(Profile entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setDescrible(entity.getDescrible() != null ? entity.getDescrible() : "");
        return dto;
    }

    public Profile dtoToEntity(ProfileDTO dto) {
        Profile c = new Profile();
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }

    public Profile requestDtoToEntity(ProfileRequestDTO dto) {
        Profile c = new Profile();
        c.setDescrible(dto.getDescrible() != null ? dto.getDescrible() : "");
        return c;
    }
}
