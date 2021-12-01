package com.dminer.converters;


import com.dminer.dto.UserDTO;
import com.dminer.entities.User;
import com.dminer.services.UserService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

	
	@Autowired
    private UserService userService;
	
	private String token = null;
	
    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        Optional<User> opt = userService.findByLogin(dto.getLogin());
        if (opt.isPresent()) {
        	user.setId(opt.get().getId());        	
        }
        user.setLogin(dto.getLogin());
        return user;
    }


    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        Optional<User> opt = userService.findByLogin(dto.getLogin());
        if (opt.isPresent()) {
        	user.setId(opt.get().getId());        	
        }
        
        dto.setLogin(user.getLogin());
        if (token == null) {
            token = userService.getToken();
        }
//        String avatar = userService.getAvatar(dto.getLogin(), token);
//        dto.setAvatar(avatar);
        return dto;
    }
   
}
