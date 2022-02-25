package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dminer.constantes.MessagesConst;
import com.dminer.converters.BenefitsConverter;
import com.dminer.dto.BenefitsRequestDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.BenefitsDTO;
import com.dminer.entities.Benefits;
import com.dminer.repository.BenefitsRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
import com.dminer.services.BenefitsService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;
import com.dminer.validadores.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/birthday")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class BirthdayController {
    
    private static final Logger log = LoggerFactory.getLogger(BirthdayController.class);


    @Autowired
    private UserService userService;


    @GetMapping(value = "/search/{login}/{keyword}")
    public ResponseEntity<Response<List<UserDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();

        if (token.naoPreenchido()) {
            response.getErrors().add("Token precisa ser informado");    		
    		ResponseEntity.ok().body(response);
        }

        if (keyword.equalsIgnoreCase("null")) keyword = null;
    	UserRestModel userRestModel = userService.carregarUsuariosApi(token.getToken());

        if (userRestModel == null) {
    		response.getErrors().add("Nenhum usuario encontrado");    		
    		ResponseEntity.ok().body(response);
    	}
        
        if (userRestModel.hasError()) {
        	userRestModel.getOutput().getMessages().forEach(u -> {
    			response.getErrors().add(u);
    		});
        	ResponseEntity.ok().body(response);
        }

        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        for (Usuario u : userRestModel.getOutput().getResult().getUsuarios()) {
            if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
                aniversariantes.add(u.toUserDTO());
            }            
        }
        
        aniversariantes.forEach(a -> {
            String avatar = userService.getAvatarBase64ByLogin(a.getLogin());
            a.setAvatar(avatar);
        });
        
        if (keyword != null) {
            aniversariantes = userService.search(keyword, aniversariantes);
            if (aniversariantes.isEmpty()) {
                response.getErrors().add("Nenhum aniversariante encontrado");
                ResponseEntity.ok().body(response);
            }
        }

        response.setData(aniversariantes);
        return ResponseEntity.ok().body(response);
    }
}
