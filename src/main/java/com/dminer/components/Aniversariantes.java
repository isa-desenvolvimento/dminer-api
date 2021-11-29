package com.dminer.components;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dminer.controllers.ServerSendEvents;
import com.dminer.dto.UserDTO;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

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

        String token = userService.getToken();
        List<UserDTO> usuarios = userService.carregarUsuariosApi(token).getData();
        List<UserDTO> aniversariantes = new ArrayList<>();
        Timestamp firstDay = UtilDataHora.currentFirstDayTimestamp();
        Timestamp lastDay = UtilDataHora.currentLastDayTimestamp();

        for (UserDTO userDTO : usuarios) {
            Date date = UtilDataHora.stringToDate(userDTO.getBirthDate());
            if (date.getTime() >= firstDay.getTime() || date.getTime() <= lastDay.getTime()) {
                aniversariantes.add(userDTO);
            }
        }

        if (!aniversariantes.isEmpty()) {
            aniversariantes.forEach(ani -> {
                serverSendEvents.addAniversariantes(ani);
            });
            serverSendEvents.streamSseBirthday();
        }

    }

}
