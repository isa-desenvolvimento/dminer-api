package com.dminer.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.components.TokenService;
import com.dminer.response.Response;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class Token {

   
	@PostMapping()
    public ResponseEntity<Response<String>> create(@Valid @RequestParam String senha) {

        Response<String> response = new Response<>();

        if (senha == null || senha.isEmpty() || !senha.equals("Pa$$")) {
        	response.addError("Senha inválida");
        	return ResponseEntity.badRequest().body(response);            
        }
        response.setData(TokenService.getToken());
        return ResponseEntity.ok().body(response);
    }
	
}
