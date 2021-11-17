package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.Permission;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConverter {

    @Autowired
    private PermissionRepository permissionRepository;


    public User dtoToEntity(UserDTO dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setArea(dto.getArea());
        user.setEmail(dto.getEmail());
        user.setLinkedin(dto.getLinkedin());
        if (dto.getPermission() != null) {
            user.setPermission(new PermissionConverter().dtoToEntity(dto.getPermission()));
        }
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
        if (dto.getPermission() != null) {
            Optional<Permission> p = permissionRepository.findById(dto.getPermission());
            if (p.isPresent())
                user.setPermission(p.get());
        }
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
        if (user.getPermission() != null)
            dto.setPermission(new PermissionConverter().entityToDTO(user.getPermission()));
        return dto;
    }
   
}
