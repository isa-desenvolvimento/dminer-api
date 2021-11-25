package com.dminer.converters;


import com.dminer.dto.UserDTO;
import com.dminer.entities.User;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setLogin(dto.getLogin());
        user.setBanner(dto.getBanner());
        return user;
    }
    
    // public User requestDtoToEntity(UserRequestDTO dto) {
    //     User user = new User();        
    //     user.setLogin(dto.getLogin());
    //     user.setBanner(dto.getBanner());
    //     return user;
    // }

    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        if (user.getBanner() != null)
            dto.setBanner(user.getBanner());
        return dto;
    }
   
}
