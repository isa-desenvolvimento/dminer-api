package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.NoticeDTO;
import com.dminer.dto.NoticeRequestDTO;
import com.dminer.dto.UserReductDTO;
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
            dto.getUsers().add(new UserReductDTO(user.getLogin()));
        });
        dto.setWarning(aviso.getWarning());
        dto.setCreator(aviso.getCreator());
        dto.setPriority(aviso.getPriority());
        dto.setDate(aviso.getDate() != null ? UtilDataHora.dateToFullStringUTC(aviso.getDate()) : null);
        dto.setActive(aviso.getActive());
        return dto;
    }

    public Notice dtoToEntity(NoticeDTO avisoDto) {
        Notice c = new Notice();
        c.setId(avisoDto.getId());
        c.setDate(avisoDto.getDate() != null ? UtilDataHora.toTimestamp(avisoDto.getDate()) : null);
        avisoDto.getUsers().forEach(usuario -> {
            Optional<User> user = userService.findByLogin(usuario.getLogin());
            if (user.isPresent())
                c.getUsers().add(user.get());
        });
        c.setWarning(avisoDto.getWarning());
        c.setCreator(avisoDto.getCreator());        
        c.setPriority(avisoDto.getPriority());
        c.setActive( avisoDto.getActive() == null ? true : avisoDto.getActive() );
        return c;
    }

    public Notice requestDtoToEntity(NoticeRequestDTO avisoDto) {
        Notice c = new Notice();
        c.setDate(avisoDto.getDate() != null ? UtilDataHora.toTimestamp(avisoDto.getDate()) : null);
        avisoDto.getUsers().forEach(usuario -> {
            Optional<User> user = userService.findByLogin(usuario);
            if (user.isPresent())
                c.getUsers().add(user.get());
        });
        c.setWarning(avisoDto.getWarning());
        c.setCreator(avisoDto.getCreator());
        c.setPriority(avisoDto.getPriority());
        c.setActive(true);
        return c;
    }
}
