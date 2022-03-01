package com.dminer.controllers;

import java.util.List;

import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.response.Response;
import com.dminer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

        if (token == null || token.getToken().isBlank()) {
            response.getErrors().add("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        List<UserDTO> aniversariantes = userService.getAniversariantes(token.getToken(), true);
        
        if (keyword != null) {
            aniversariantes = userService.search(aniversariantes, keyword);
            if (aniversariantes.isEmpty()) {
                response.getErrors().add("Nenhum aniversariante encontrado");
                return ResponseEntity.ok().body(response);
            }
        }

        response.setData(aniversariantes);
        return ResponseEntity.ok().body(response);
    }
}
