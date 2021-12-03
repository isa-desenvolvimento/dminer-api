package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.ws.rs.HeaderParam;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.dminer.converters.UserConverter;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

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

    private String token;
    


    private void validateDto(UserDTO userDTO, BindingResult result) {        
        if (userDTO.getLogin() == null) {
            result.addError(new ObjectError("userDTO", "Login precisa estar preenchido."));			
        }
    }

    @GetMapping
    public void teste() {
        
//    	String token = userService.getToken();
//        userService.carregarUsuariosApi3(token);
        
    }
    
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
    public ResponseEntity<Response<List<UserDTO>>> getAll(@RequestBody Token token) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (token == null) {
        	response.getErrors().add("Token precisa ser informado");
        }
        
        List<UserDTO> userList = userService.carregarUsuariosApi2(token.getToken());
        if (userList == null) {
        	response.getErrors().add("Token inválido ou expirado!");
        }
        
        if (userList.isEmpty()) {
            response.getErrors().add("Usuários não encontrados");
        }
        
        if (!response.getErrors().isEmpty()) {
        	return ResponseEntity.badRequest().body(response);        	
        }        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }
    
    
    @PostMapping(value = "/dropdown")
    public ResponseEntity<Response<List<UserReductDTO>>> getDropDown(@RequestBody Token token) {
    	
    	System.out.println(token.getToken());
    	
        Response<List<UserReductDTO>> response = new Response<>();
        if (token != null) {
        	List<UserReductDTO> users = userService.carregarUsuariosApiReduct(token.getToken());
        	
        	if (users == null || users.isEmpty()) {
        		response.getErrors().add("Nenhum usuario encontrado");
        		return ResponseEntity.badRequest().body(response);
        	}
        	response.setData(users);
        }
        return ResponseEntity.ok().body(response);
    }
    
    

    @GetMapping("/birthdays")
    public ResponseEntity<Response<List<UserDTO>>> getBirthDaysOfMonth() {
        
        Response<List<UserDTO>> response = new Response<>();

        if (token == null) {
        	token = userService.getToken();
        }
        
        UserRestModel model = userService.carregarUsuariosApi(token);
        if (model == null || model.hasError()) {
    		response.getErrors().add("Nenhum usuario encontrado");
    		model.getOutput().getMessages().forEach(u -> {
    			response.getErrors().add(u);
    		});
    		return ResponseEntity.badRequest().body(response);
    	}
        
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        model.getOutput().getResult().getUsuarios().forEach(u -> {
        	if (UtilDataHora.isAniversariante(u.getBirthDate())) {
        		aniversariantes.add(u.toUserDTO());
        	}
        });
        
        if (aniversariantes.isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(aniversariantes);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
