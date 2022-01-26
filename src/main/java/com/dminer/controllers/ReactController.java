package com.dminer.controllers;

import java.util.List;

import com.dminer.entities.React;
import com.dminer.repository.ReactRepository;
import com.dminer.response.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/react")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReactController {
    
    @Autowired
    private ReactRepository reactRepository;


    @GetMapping("/dropdown")
	public ResponseEntity<Response<List<React>>> reacts() {
		
		Response<List<React>> response = new Response<>();
        List<React> reacts = reactRepository.findAll();

        if (reacts.isEmpty()) {
            response.addError("Nenhum react encontrado");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);    
        }
        
        //Arrays.asList("D-TERMINADO", "D-MAIS", "D-SLUMBRADO", "D-SACREDITADO", "D-IVERTIDO")
		response.setData(reacts);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
}
