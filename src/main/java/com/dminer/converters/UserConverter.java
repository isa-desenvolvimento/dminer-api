package com.dminer.converters;


import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.UserService;

import java.util.List;
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
        user.setUserName(dto.getUserName());
        user.setAvatar(dto.getAvatar());
        user.setBanner(dto.getBanner());
        return user;
    }


    public UserDTO entityToDto(User user) {
        UserDTO dto = new UserDTO();
        Optional<User> opt = userService.findByLogin(dto.getLogin());
        if (opt.isPresent()) {
        	user.setId(opt.get().getId());        	
        }
        
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        dto.setBanner(user.getBanner());
        return dto;
    }
    
    
    public UserReductDTO entityToUserReductDTO(User user) {
        UserReductDTO dto = new UserReductDTO();
     
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        String avatar = userService.getAvatarBase64ByLogin(user.getLogin());
        dto.setAvatar(avatar);
        return dto;
    }

      
}
