package com.dminer.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.dto.PostDTO;
import com.dminer.response.Response;
import com.dminer.services.FeedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feed")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FeedController {
    
    @Autowired
    private FeedService feedService;



    @GetMapping("/all")
    public ResponseEntity<Response<List<PostDTO>>> getAll() {
        
        Response<List<PostDTO>> response = new Response<>();

        List<PostDTO> user = feedService.getAll();
        if (user.isEmpty()) {
            response.getErrors().add("Nenhum dado encontrado");
            return ResponseEntity.ok().body(response);
        }

		response.setData(user);
        return ResponseEntity.ok().body(response);
    }
    
}
