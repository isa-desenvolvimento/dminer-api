package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;

import com.dminer.dto.PostDTO;
import com.dminer.dto.PostReductDTO;
import com.dminer.entities.Post;
import com.dminer.response.Response;
import com.dminer.services.PostService;

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
    
    @Autowired
    private PostService postService;



    @GetMapping("/all")
    public ResponseEntity<Response<List<PostReductDTO>>> getAll() {
        
        Response<List<PostReductDTO>> response = new Response<>();

        List<Post> user = postService.findAll();
        if (user.isEmpty()) {
            response.getErrors().add("Nenhum dado encontrado");
            return ResponseEntity.status(404).body(response);
        }

        List<PostReductDTO> eventos = new ArrayList<>();
        user.forEach(u -> {
            eventos.add(postToDto(u));
        });

		response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }

    private PostReductDTO postToDto(Post post) {
    	PostReductDTO dto = new PostReductDTO();
//		dto.setLogin(post.getLogin());
//		dto.setLikes(post.getLikes());
//		if (post.getType() != null)
//			dto.setType(post.getType().name());
//		dto.setId(post.getId());		
		dto.setContent(post.getContent());
		dto.setTitle(post.getTitle());
		return dto;
	}
}
