package com.dminer.converters;

import java.util.Optional;

import com.dminer.dto.AvisosDTO;
import com.dminer.dto.AvisosRequestDTO;
import com.dminer.entities.Avisos;
import com.dminer.entities.User;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvisosConverter {

    @Autowired
    private UserService userService;

    public AvisosDTO entityToDTO(Avisos aviso) {
        AvisosDTO dto = new AvisosDTO();
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

    public Avisos dtoToEntity(AvisosDTO avisoDto) {
        Avisos c = new Avisos();
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

    public Avisos requestDtoToEntity(AvisosRequestDTO avisoDto) {
        Avisos c = new Avisos();
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
