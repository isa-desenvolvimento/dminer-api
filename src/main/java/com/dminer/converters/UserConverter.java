package com.dminer.converters;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.User;
import com.dminer.utils.UtilDataHora;

import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setDtBirthday(UtilDataHora.stringToDate(dto.getDtBirthday()));
        return user;
    }
    
    public User requestDtoToEntity(UserRequestDTO dto) {
        User user = new User();        
        user.setName(dto.getName());
        user.setDtBirthday(UtilDataHora.stringToDate(dto.getDtBirthday()));
        return user;
    }

    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        if (user.getDtBirthday() != null)
            dto.setDtBirthday(UtilDataHora.dateToString(user.getDtBirthday()));
        dto.setAvatar(user.getAvatar().getUrl());
        dto.setBanner(user.getBanner().getUrl());
        return dto;
    }
}
