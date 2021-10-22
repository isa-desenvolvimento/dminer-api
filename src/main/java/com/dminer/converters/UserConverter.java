package com.dminer.converters;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.User;

import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        return user;
    }
    
    public User requestDtoToEntity(UserRequestDTO dto) {
        User user = new User();        
        user.setName(dto.getName());
        return user;
    }

    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }
}
