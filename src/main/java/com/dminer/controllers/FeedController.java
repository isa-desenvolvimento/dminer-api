package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;

import com.dminer.dto.PostDTO;
import com.dminer.entities.Post;
import com.dminer.response.Response;
import com.dminer.services.PostService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feed")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class FeedController {
    
    private static final Logger log = LoggerFactory.getLogger(FeedController.class);

    @Autowired
    private PostService postService;


    @GetMapping("/all")
    public ResponseEntity<Response<List<PostDTO>>> getAll() {
        
        Response<List<PostDTO>> response = new Response<>();

        List<Post> user = postService.findAll();
        if (user.isEmpty()) {
            response.getErrors().add("Feed não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        List<PostDTO> eventos = new ArrayList<>();
        user.forEach(u -> {
            eventos.add(postToDto(u));
        });

		response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }

    private PostDTO postToDto(Post post) {
		PostDTO dto = new PostDTO();
		dto.setIdUsuario(post.getId());
		dto.setLikes(post.getLikes());
		dto.setType(post.getType().name());
		dto.setId(post.getId());		
		dto.setContent(post.getContent());
		return dto;
	}
}
