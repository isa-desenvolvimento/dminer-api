package com.dminer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.UserConverter;
import com.dminer.dto.UserReductDTO;
import com.dminer.response.Response;
import com.dminer.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/drop-down")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class Dropdown {

	@Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;
    
    
    @PostMapping(value = "/users/all")
    public ResponseEntity<Response<List<UserReductDTO>>> getAllUsers(@RequestParam("token") String token) {
    	
        Response<List<UserReductDTO>> response = new Response<>();
        if (token == null) {
        	response.getErrors().add("Token precisa ser informado");
    		return ResponseEntity.badRequest().body(response);
        }
        
    	Response<List<UserReductDTO>> opt = userService.carregarUsuariosApiReduct(token);
    	if (opt.getData().isEmpty()) {
    		response.getErrors().add("Usuários não encontrados");
    		return ResponseEntity.badRequest().body(response);
    	}
      	response.setData(opt.getData());
        return ResponseEntity.ok().body(response);
    }
    
    
}
