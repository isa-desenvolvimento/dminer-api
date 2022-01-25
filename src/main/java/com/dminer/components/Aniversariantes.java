package com.dminer.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.dminer.controllers.ServerSendEvents;
import com.dminer.dto.UserDTO;
import com.dminer.rest.model.users.Usuario;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class Aniversariantes {

    private static final Logger log = LoggerFactory.getLogger(Aniversariantes.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ServerSendEvents serverSendEvents;


    @Scheduled(cron  = "0 0 0 1 1 *")
    public void aniversariantesDoMes() {

        log.info("Verificando aniversariantes");

        String token = TokenService.getToken();
        List<UserDTO> usuarios = userService.carregarUsuariosApi(token).getUsers().stream().map(Usuario::toUserDTO).collect(Collectors.toList());
        List<UserDTO> aniversariantes = new ArrayList<>();
        
        log.info("Quantidade de usuários carregados da api: {}", usuarios.size());

        for (UserDTO userDTO : usuarios) {            
            if (UtilDataHora.isAniversariante(userDTO.getBirthDate())) {
                aniversariantes.add(userDTO);
            }
        }

        if (!aniversariantes.isEmpty()) {
            log.info("Quantidade de aniversariantes encontrados: {}", aniversariantes.size());
            aniversariantes.forEach(ani -> {
                serverSendEvents.addAniversariantes(ani);
                log.info("Aniversariante: {}", ani.getUserName());
            });
            serverSendEvents.streamSseBirthday();
        }
    }
}
