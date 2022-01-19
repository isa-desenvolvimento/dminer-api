package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.dto.FavoriteDTO;
import com.dminer.dto.FavoriteRequestDTO;
import com.dminer.entities.Favorites;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.repository.FavoritesRepository;
import com.dminer.response.Response;
import com.dminer.services.PostService;
import com.dminer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/favorite")
@CrossOrigin(origins = "*")
public class FavoriteController {
    
    private static final Logger log = LoggerFactory.getLogger(FavoriteController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private FavoritesRepository favoritesRepository;


    private void validateRequestDto(FavoriteRequestDTO dto, BindingResult result) {
        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            result.addError(new ObjectError("FavoriteRequestDTO", "Login do usuário precisa estar preenchido."));
		} else {
            Optional<User> findById = userService.findByLogin(dto.getLogin());
            if (!findById.isPresent()) {
                result.addError(new ObjectError("FavoriteRequestDTO", "Usuário: "+ dto.getLogin() +" não encontrado."));
            }
        }

        if (dto.getIdPost() == null)  {
            result.addError(new ObjectError("dto", "Id do Post precisa estar preenchido."));
		} else {
			Optional<Post> opt = postService.findById(dto.getIdPost()); 
            if (!opt.isPresent()) {
                result.addError(new ObjectError("dto", "Post não encontrado."));
            }
        }
    }

    
    @PostMapping
    public ResponseEntity<Response<FavoriteDTO>> create(@Valid @RequestBody FavoriteRequestDTO dtoReq, BindingResult result) {
        
        log.info("Salvando um novo favorito {}", dtoReq.toString());

        Response<FavoriteDTO> response = new Response<>();

        validateRequestDto(dtoReq, result);
        if (result.hasErrors()) {
            log.info("Erro validando dtoReq: {}", dtoReq);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> user = userService.findByLogin(dtoReq.getLogin());        
        Optional<Favorites> favos = favoritesRepository.findByUserAndPost(user.get(), new Post(dtoReq.getIdPost()));
        if (favos.isPresent()) {
            favoritesRepository.deleteById(favos.get().getId());
            return ResponseEntity.ok().build();
        }

        Favorites favorite = new Favorites();
        favorite.setPost(new Post(dtoReq.getIdPost()));        
        favorite.setUser(user.get());
        favorite = favoritesRepository.save(favorite);

        FavoriteDTO dto = new FavoriteDTO();
        dto.setId(favorite.getId());
        dto.setIdPost(dtoReq.getIdPost());
        dto.setLogin(dtoReq.getLogin());

        response.setData(dto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/all-by-user/{login}")
    public ResponseEntity<Response<List<FavoriteDTO>>> allByUser(@PathVariable String login) {
        
        log.info("Buscando todos por usuário {}", login);

        Response<List<FavoriteDTO>> response = new Response<>();

        if (login == null || login.isBlank()) {
            response.setErrors(Arrays.asList("Informe o login do usuário"));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> user = userService.findByLogin(login);
        if (!user.isPresent()) {
            response.setErrors(Arrays.asList("Nenhum favorito encontrado"));
            return ResponseEntity.badRequest().body(response);
        }

        List<Favorites> favos = favoritesRepository.findAllByUser(user.get());
        if (favos.isEmpty()) {
            response.setErrors(Arrays.asList("Nenhum favorito encontrado"));
            return ResponseEntity.status(404).body(response);
        }

        List<FavoriteDTO> favosDto = new ArrayList<>();
        favos.forEach(f -> {
            FavoriteDTO dto = new FavoriteDTO();
            dto.setId(f.getId());
            dto.setIdPost(f.getPost().getId());
            dto.setLogin(f.getUser().getLogin());
            favosDto.add(dto);
        });

        response.setData(favosDto);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/all-by-post/{idPost}")
    public ResponseEntity<Response<List<FavoriteDTO>>> allByPost(@PathVariable Integer idPost) {
        
        log.info("Buscando todos por post {}", idPost);

        Response<List<FavoriteDTO>> response = new Response<>();

        if (idPost == null) {
            response.setErrors(Arrays.asList("Informe o id do post"));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Post> post = postService.findById(idPost);
        if (!post.isPresent()) {
            response.setErrors(Arrays.asList("Nenhum favorito encontrado"));
            return ResponseEntity.badRequest().body(response);
        }

        List<Favorites> favos = favoritesRepository.findAllByPost(new Post(idPost));
        if (favos.isEmpty()) {
            response.setErrors(Arrays.asList("Nenhum favorito encontrado"));
            return ResponseEntity.status(404).body(response);
        }

        List<FavoriteDTO> favosDto = new ArrayList<>();
        favos.forEach(f -> {
            FavoriteDTO dto = new FavoriteDTO();
            dto.setId(f.getId());
            dto.setIdPost(f.getPost().getId());
            dto.setLogin(f.getUser().getLogin());
            favosDto.add(dto);
        });

        response.setData(favosDto);
        return ResponseEntity.ok(response);
    }

}