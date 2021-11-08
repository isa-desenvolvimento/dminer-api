package com.dminer.converters;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.User;
import com.dminer.enums.Profiles;
import com.dminer.utils.UtilDataHora;

import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setArea(dto.getArea());
        user.setEmail(dto.getEmail());
        user.setLinkedin(dto.getLinkedin());
        user.setProfile(Profiles.valueOf(dto.getProfile()));
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
        user.setProfile(Profiles.valueOf(dto.getProfile()));
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
        dto.setProfile(user.getProfile().name());
        return dto;
    }
   
}
