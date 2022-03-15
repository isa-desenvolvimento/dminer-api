package com.dminer.converters;


import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.services.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

	
	@Autowired
    private UserService userService;
	
	
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
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        dto.setBanner(user.getBanner());
        return dto;
    }
    
    public UserDTO entityToDto(User user, String avatar) {
        UserDTO dto = new UserDTO();
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        dto.setBanner(user.getBanner());
        dto.setAvatar(avatar);
        return dto;
    }
    
    public UserReductDTO entityToUserReductDTO(User user) {
        UserReductDTO dto = new UserReductDTO();     
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        return dto;
    }

    public UserReductDTO entityToUserReductDTO(User user, String avatar) {
        UserReductDTO dto = new UserReductDTO();     
        dto.setLogin(user.getLogin());
        dto.setUserName(user.getUserName());
        dto.setAvatar(avatar);
        return dto;
    }

    public UserReductDTO entityToUserReductDTO(List<User> users, UserRestModel<UserAvatar> avatares) {
        for (User user : users) {
            return entityToUserReductDTO(user, getAvatarByUsername(avatares, user.getUserName()));
        }
        return null;
    }

    private String getAvatarByUsername(UserRestModel<UserAvatar> usuarios, String userName) {
		UserAvatar userAvatar = usuarios.getUsuarios().stream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
		if (userAvatar == null || userAvatar.isCommonAvatar()) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}
    
}
