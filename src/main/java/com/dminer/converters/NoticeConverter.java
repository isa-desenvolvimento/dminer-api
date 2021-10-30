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
        aviso.getUsuarios().forEach(user -> {
            dto.getUsuarios().add((user.getId()));     
        });
        dto.setAviso(aviso.getAviso());
        dto.setCriador(aviso.getCriador());
        dto.setPrioridade(aviso.getPrioridade());
        dto.setData(aviso.getData() != null ? UtilDataHora.hourToString(aviso.getData()) : null);
        return dto;
    }

    public Notice dtoToEntity(NoticeDTO avisoDto) {
        Notice c = new Notice();
        c.setId(avisoDto.getId());
        c.setData(avisoDto.getData() != null ? UtilDataHora.toTimestamp(avisoDto.getData()) : null);
        avisoDto.getUsuarios().forEach(usuario -> {
            Optional<User> user = userService.findById(usuario);
            if (user.isPresent())
                c.getUsuarios().add(user.get());
        });
        c.setAviso(avisoDto.getAviso());
        c.setCriador(avisoDto.getCriador());        
        c.setPrioridade(avisoDto.getPrioridade());
        return c;
    }

    public Notice requestDtoToEntity(NoticeRequestDTO avisoDto) {
        Notice c = new Notice();
        c.setData(avisoDto.getData() != null ? UtilDataHora.toTimestamp(avisoDto.getData()) : null);
        avisoDto.getUsuarios().forEach(usuario -> {
            Optional<User> user = userService.findById(usuario);
            if (user.isPresent())
                c.getUsuarios().add(user.get());
        });
        c.setAviso(avisoDto.getAviso());
        c.setCriador(avisoDto.getCriador());        
        c.setPrioridade(avisoDto.getPrioridade());
        return c;
    }
}
