package com.dminer.controllers;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.dto.PostDTO;
import com.dminer.dto.PostReductDTO;
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

    @Autowired
    private Environment env;

    private static final Logger log = LoggerFactory.getLogger(FeedController.class);

    @GetMapping(value = "/search/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<List<PostReductDTO>>> search(@RequestHeader("x-access-token") String token, @PathVariable("login") String login, @PathVariable("keyword") String keyword) {
        
        Response<List<PostReductDTO>> response = new Response<>();

        if (keyword.equalsIgnoreCase("null")) keyword = null;

        List<PostReductDTO> search = feedService.search(keyword, login, isProd());
        log.info("Search do feed {} resultados ", search.size());

        response.setData(search); 
        return ResponseEntity.ok().body(response);
    }


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
    
    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
