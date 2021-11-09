package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.Profile;
import com.dminer.entities.User;
import com.dminer.repository.ProfileRepository;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    @Autowired
    private ProfileRepository profileRepository;

    
    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setArea(dto.getArea());
        user.setEmail(dto.getEmail());
        user.setLinkedin(dto.getLinkedin());
        user.setProfile(new ProfileConverter().dtoToEntity(dto.getProfile()));
        user.setDtBirthday(UtilDataHora.toTimestamp(dto.getDtBirthday()));
        user.setAvatar(dto.getAvatar());
        user.setBanner(dto.getBanner());
        return user;
    }
    
    public User requestDtoToEntity(UserRequestDTO dto) {
        User user = new User();        
        user.setName(dto.getName());
        user.setArea(dto.getArea());
        user.setEmail(dto.getEmail());
        user.setLinkedin(dto.getLinkedin());
        Optional<Profile> p = profileRepository.findById(dto.getProfile());
        if (p.isPresent())
            user.setProfile(p.get());
        user.setDtBirthday(UtilDataHora.toTimestamp(dto.getDtBirthday()));
        user.setAvatar(dto.getAvatar());
        user.setBanner(dto.getBanner());
        return user;
    }

    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getDtBirthday() != null)
            dto.setDtBirthday(UtilDataHora.timestampToString(user.getDtBirthday()));
        if (user.getAvatar() != null)
            dto.setAvatar(user.getAvatar());
        if (user.getBanner() != null)
            dto.setBanner(user.getBanner());
        dto.setArea(user.getArea());
        dto.setEmail(user.getEmail());
        dto.setLinkedin(user.getLinkedin());
        if (user.getProfile() != null)
            dto.setProfile(new ProfileConverter().entityToDTO(user.getProfile()));
        return dto;
    }
   
}
