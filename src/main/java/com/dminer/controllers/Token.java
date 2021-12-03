package com.dminer.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.dto.CommentDTO;
import com.dminer.dto.CommentRequestDTO;
import com.dminer.entities.Comment;
import com.dminer.response.Response;
import com.dminer.services.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/token")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class Token {

	@Autowired
    private UserService userService;
	
	@PostMapping()
    public ResponseEntity<Response<String>> create(@Valid @RequestParam String senha) {

        Response<String> response = new Response<>();

        if (senha == null || senha.isEmpty() || !senha.equals("Pa$$")) {
        	response.getErrors().add("Senha inválida");
        	ResponseEntity.badRequest().body(response);
        }
        response.setData(userService.getToken());
        return ResponseEntity.ok().body(response);
    }
	
}
