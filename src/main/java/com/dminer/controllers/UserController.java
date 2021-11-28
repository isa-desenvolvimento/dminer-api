package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dminer.converters.UserConverter;
import com.dminer.dto.UserDTO;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.UserRestModel;
import com.dminer.services.UserService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private Environment env;


    //@GetMapping("/rest/all")
    public ResponseEntity<Response<List<UserRestModel>>> getUsersRest2() {
    	
    	String uri = "https://www.dminerweb.com.br:8553/api/administrative/client_area/user/select_user";
    	RestTemplate template = new RestTemplate();
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    	headers.add("BAERER_AUTHENTICATION", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvYmplY3QiOiJ7XCJpZFwiOlwiO1gzQkRcXHUwMDNjQUdCUUtFOFJcXHUwMDNjTzRhWnJXc0RNdTkzcm1QM1BhSkJMTGtsNjhReHpDNnBhZVFtWCNuSmFaVmhWISQxOVwiLFwidHlwZVwiOlwiZ2FtaWZpY2Fkb1wiLFwidXNlclwiOlwibWF0aGV1cy5yaWJlaXJvMVwiLFwibG9naW5UaW1lXCI6XCJOb3YgMjYsIDIwMjEsIDM6MjA6MjggUE1cIixcImV4cGlyZVRva2VuXCI6XCJOb3YgMjYsIDIwMjEsIDM6NTA6MjggUE1cIixcInNlc3Npb25JbmZpbml0eVwiOmZhbHNlLFwiYXR0cmlidXRlc1wiOntcInRva2VuX2hhc2hpZHNcIjpcIjhkZDNlYTgyLWNlMGQtNGRjMi04MjMwLWViOWQ5YjZjYjg1NlwiLFwicmFuZG9tX2lkZW50aWZ5XCI6XCJmY2ViZWUwMy0yNjhlLTQ1NWYtODg5My1lMWZmMjUzYWQ2YzJcIixcImlwXCI6XCIxOTEuMjIzLjIzOS4xMDJcIn19In0.NHXi5-sp_dqeQ6bBnTEvUj3C_zl_ee59r6HvPdqpgyE");

    	HttpEntity<String> entity = new HttpEntity<>("body", headers);
    	
    	ResponseEntity<String> response = template.exchange(uri, HttpMethod.GET, entity, String.class);
    	System.out.println(response);
    	//String result = template.getForObject(uri, String.class);
    	//System.out.println(result);
    	
//        List<UserRestModel> buscarUsuarios = userRest.buscarUsuarios();
//        if (buscarUsuarios == null) {
//            System.out.println("\n\n usuarios vieram null\n\n");
//        } else {
//            buscarUsuarios.forEach(usuario -> {
//                System.out.println(usuario.getLogin());
//            });
//        }
        return null;
    }


    @GetMapping("/rest/all")
    public ResponseEntity<Response<List<User>>> getUsersRest() {
    	
    	String token = userService.getToken();
    	Response<List<User>> retorno = userService.carregarUsuariosApi(token);
    	List<User> usuarios = retorno.getData();
    	Response<List<User>> response = new Response<>();
    	 
    	usuarios.forEach(usuario -> {
    		String login = usuario.getLogin();
    		String avatar = userService.getAvatar(login, token);
    		usuario.setAvatar(avatar);
    	});
    	response.setData(usuarios);
    	return ResponseEntity.ok(response);        
    }



    private void validateDto(UserDTO userDTO, BindingResult result) {        
        if (userDTO.getLogin() == null) {
            result.addError(new ObjectError("userDTO", "Login precisa estar preenchido."));			
        }
    }


    @PostMapping()
    public ResponseEntity<Response<UserDTO>> persist(@Valid @RequestBody UserDTO dto, BindingResult result) {        

	    log.info("Persistindo um usuário {}", dto.getLogin());

        Response<UserDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando userRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        if (userService.existsByLogin(dto.getLogin())) {            
            Optional<User> findByLogin = userService.findByLogin(dto.getLogin());
            User user = findByLogin.get();
            user.setBanner(dto.getBanner());
            user = userService.persist(user);
            response.setData(userConverter.entityToDto(user));
            return ResponseEntity.ok().body(response);
        }

        User u = userConverter.dtoToEntity(dto);
        User user = userService.persist(u);
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);
    }


    // @PutMapping()
    // public ResponseEntity<Response<UserDTO>> put( @Valid @RequestBody UserDTO userDto, BindingResult result) {

    //     log.info("Alterando um usuário {}", userDto);

    //     Response<UserDTO> response = new Response<>();

    //     validateDto(userDto, result);
    //     if (result.hasErrors()) {
    //         log.info("Erro validando UserRequestDTO: {}", userDto);
    //         result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
    //         return ResponseEntity.badRequest().body(response);
    //     }

    //     User user = userService.persist(userConverter.dtoToEntity(userDto));
    //     response.setData(userConverter.entityToDto(user));
    //     return ResponseEntity.ok().body(response);
    // }


    @GetMapping(value = "/{login}")
    public ResponseEntity<Response<UserDTO>> get(@PathVariable("login") String login) {
        log.info("Buscando usuário {}", login);
        
        Response<UserDTO> response = new Response<>();
        if (login == null || login.isEmpty()) {
            response.getErrors().add("Informe um login");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> user = userService.findByLogin(login);
        if (!user.isPresent()) {
            response.getErrors().add("Usuário não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(userConverter.entityToDto(user.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<Response<List<UserDTO>>> getAll() {
        
        Response<List<UserDTO>> response = new Response<>();

        Optional<List<User>> user = userService.findAll();
        if (user.get().isEmpty()) {
            response.getErrors().add("Usuários não encontrados");
            return ResponseEntity.badRequest().body(response);
        }

        List<UserDTO> usuarios = new ArrayList<>();
        user.get().forEach(u -> {
            usuarios.add(userConverter.entityToDto(u));
        });
        response.setData(usuarios);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        log.info("Deletando usuário {}", id);
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {userService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Usuário não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/birthdays")
    public ResponseEntity<Response<List<UserDTO>>> getBirthDaysOfMonth() {
        
        Response<List<UserDTO>> response = new Response<>();

        Optional<List<UserDTO>> user;
        if (isProd()) {
            user = userService.getBirthDaysOfMonthPostgres();    
        } else {
            user = userService.getBirthDaysOfMonth();
        }
        
        if (user.get().isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(user.get());
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
