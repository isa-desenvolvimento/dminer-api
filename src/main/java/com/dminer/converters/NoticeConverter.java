package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.NoticeDTO;
import com.dminer.dto.NoticeRequestDTO;
import com.dminer.entities.Notice;
import com.dminer.entities.User;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoticeConverter {

    @Autowired
    private UserService userService;

    public NoticeDTO entityToDTO(Notice aviso) {
        NoticeDTO dto = new NoticeDTO();
        dto.setId(aviso.getId());
        aviso.getUsers().forEach(user -> {
            dto.getUsers().add((user.getId()));     
        });
        dto.setWarning(aviso.getWarning());
        dto.setCreator(aviso.getCreator());
        dto.setPriority(aviso.getPriority());
        dto.setDate(aviso.getDate() != null ? UtilDataHora.hourToString(aviso.getDate()) : null);
        return dto;
    }

    public Notice dtoToEntity(NoticeDTO avisoDto) {
        Notice c = new Notice();
        c.setId(avisoDto.getId());
        c.setDate(avisoDto.getDate() != null ? UtilDataHora.toTimestamp(avisoDto.getDate()) : null);
        avisoDto.getUsers().forEach(usuario -> {
            Optional<User> user = userService.findById(usuario);
            if (user.isPresent())
                c.getUsers().add(user.get());
        });
        c.setWarning(avisoDto.getWarning());
        c.setCreator(avisoDto.getCreator());        
        c.setPriority(avisoDto.getPriority());
        return c;
    }

    public Notice requestDtoToEntity(NoticeRequestDTO avisoDto) {
        Notice c = new Notice();
        c.setDate(avisoDto.getDate() != null ? UtilDataHora.toTimestamp(avisoDto.getDate()) : null);
        avisoDto.getUsers().forEach(usuario -> {
            Optional<User> user = userService.findById(usuario);
            if (user.isPresent())
                c.getUsers().add(user.get());
        });
        c.setWarning(avisoDto.getWarning());
        c.setCreator(avisoDto.getCreator());        
        c.setPriority(avisoDto.getPriority());
        return c;
    }
}