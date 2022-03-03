package com.dminer.components;

import com.dminer.controllers.ServerSendEvents;
import com.dminer.services.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class Aniversariantes {

    @Autowired
    private UserService userService;

    @Autowired
    private ServerSendEvents serverSendEvents;


    @Scheduled(cron  = "0 0 0 1 1 *")
    public void aniversariantesDoMes() {

//        String token = userService.getToken();
//        List<UserDTO> usuarios = userService.carregarUsuariosApi(token).getData();
//        List<UserDTO> aniversariantes = new ArrayList<>();
//        
//        for (UserDTO userDTO : usuarios) {            
//            if (UtilDataHora.isAniversariante(userDTO.getBirthDate())) {
//                aniversariantes.add(userDTO);
//            }
//        }
//
//        if (!aniversariantes.isEmpty()) {
//            aniversariantes.forEach(ani -> {
//                serverSendEvents.addAniversariantes(ani);
//            });
//            serverSendEvents.streamSseBirthday();
//        }

    }

}
