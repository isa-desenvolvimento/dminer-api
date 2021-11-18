package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.BenefitsDTO;
import com.dminer.dto.BenefitsRequestDTO;
import com.dminer.entities.Benefits;
import com.dminer.entities.Profile;
import com.dminer.entities.User;
import com.dminer.repository.ProfileRepository;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BenefitsConverter {

    @Autowired
    private UserService userService;

    @Autowired
    private ProfileRepository profileRepository;


    public BenefitsDTO entityToDTO(Benefits entity) {
        BenefitsDTO dto = new BenefitsDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent() != null ? entity.getContent() : "");
        dto.setTitle(entity.getTitle() != null ? entity.getTitle() : "");
        dto.setDate(entity.getDate() != null ? UtilDataHora.timestampToString(entity.getDate()) : null);        
        dto.setCreator(entity.getCreator().getId());
        dto.setImage(entity.getImage());         
        dto.setProfile(new ProfileConverter().entityToDTO(entity.getProfile()));
        return dto;
    }

    public Benefits dtoToEntity(BenefitsDTO dto) {
        Benefits c = new Benefits();
        c.setId(dto.getId());
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        Optional<User> user = userService.findById(dto.getCreator());
        if (user.isPresent())
            c.setCreator(user.get());
        Optional<Profile> findById = profileRepository.findById(dto.getProfile().getId());
        if (findById.isPresent())
            c.setProfile(findById.get());
        c.setImage(dto.getImage());
        return c;
    }

    public Benefits requestDtoToEntity(BenefitsRequestDTO dto) {
        Benefits c = new Benefits();
        c.setTitle(dto.getTitle() != null ? dto.getTitle() : "");
        c.setContent(dto.getContent() != null ? dto.getContent() : "");
        c.setDate(dto.getDate() != null ? UtilDataHora.toTimestamp(dto.getDate()) : null);
        Optional<User> user = userService.findById(dto.getCreator());
        if (user.isPresent())
            c.setCreator(user.get());
        Optional<Profile> findById = profileRepository.findById(dto.getProfile());
        if (findById.isPresent())
            c.setProfile(findById.get());
        c.setImage(dto.getImage());
        return c;
    }

}
