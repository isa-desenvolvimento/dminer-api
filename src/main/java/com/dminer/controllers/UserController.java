package com.dminer.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
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

    @GetMapping("/teste")
    public void teste() throws IOException {
        
    	
        byte[] avatar = userService.getAvatar("matheus.ribeiro1");
        userService.compress(avatar);
        
    }
    
    @PostMapping(value = "/{login}")
    public ResponseEntity<Response<UserDTO>> get(@PathVariable("login") String login) {
        log.info("Buscando usuário {}", login);
        
        Response<UserDTO> response = new Response<>();
        if (login == null || login.isEmpty()) {
            response.getErrors().add("Informe um login");
            return ResponseEntity.badRequest().body(response);
        }

        if (token == null) {
        	token = userService.getToken();
        }
        
        UserRestModel model = userService.carregarUsuariosApi(token);
        if (model == null) {
        	response.getErrors().add("Token inválido ou expirado!");
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (model.hasError()) {
        	model.getOutput().getMessages().forEach(e -> {
        		response.getErrors().add(e);
        	});
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (model.getOutput().getResult().getUsuarios().isEmpty()) {
            response.getErrors().add("Usuário não encontrado");
        }
        
        byte[] banner = userService.getBanner(login);
        UserDTO dto = model.getOutput().getResult().getUsuarios().get(0).toUserDTO();
        if (banner != null) {
        	String encodedString = Base64.getEncoder().encodeToString(banner);
        	dto.setBanner(encodedString);
        }
        
        byte[] avatar = userService.getAvatar(login);
        dto = model.getOutput().getResult().getUsuarios().get(0).toUserDTO();
        if (avatar != null) {
        	String encodedString = Base64.getEncoder().encodeToString(avatar);
        	dto.setAvatar(encodedString);
        }        
        
        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/all")
    public ResponseEntity<Response<List<UserDTO>>> getAll(@RequestBody Token token) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (token == null) {
        	response.getErrors().add("Token precisa ser informado");
        }
        
        UserRestModel model = userService.carregarUsuariosApi(token.getToken());
        if (model == null) {
        	response.getErrors().add("Token inválido ou expirado!");
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (model.hasError()) {
        	model.getOutput().getMessages().forEach(e -> {
        		response.getErrors().add(e);
        	});
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (model.getOutput().getResult().getUsuarios().isEmpty()) {
            response.getErrors().add("Usuários não encontrados");
        }
        
        if (!response.getErrors().isEmpty()) {
        	return ResponseEntity.badRequest().body(response);        	
        }
        
        List<UserDTO> userList = new ArrayList<>();
        model.getOutput().getResult().getUsuarios().forEach(u -> {
        	userList.add(u.toUserDTO());
        });
        
        userList.forEach(u -> {
        	byte[] avatar = userService.getAvatar(u.getLogin());            
            if (avatar != null) {
            	String encodedString = Base64.getEncoder().encodeToString(avatar);
            	u.setAvatar(encodedString);
            }
        });
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
        if (model == null) {
    		response.getErrors().add("Nenhum usuario encontrado");    		
    		return ResponseEntity.badRequest().body(response);
    	}
        
        if (model.hasError()) {
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
