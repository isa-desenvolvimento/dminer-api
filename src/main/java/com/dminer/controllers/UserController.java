package com.dminer.controllers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.ws.rs.HeaderParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.components.TokenService;
import com.dminer.converters.UserConverter;
import com.dminer.dto.DocumentDTO;
import com.dminer.dto.PermissionUserDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.Document;
import com.dminer.entities.User;
import com.dminer.images.ImageResizer;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
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


    private String getBannerBase64(String login) {
        byte[] banner = userService.getBanner(login);
        if (banner != null) {

        	return Base64.getEncoder().encodeToString(banner);        	
        }
        return null;
    }

    private String getBannerString(String login) {
        return userService.getBannerString(login);        
    }

    @GetMapping(value = "/{login}")
    public ResponseEntity<Response<UserDTO>> get(@PathVariable("login") String login) {
        log.info("Buscando usuário {}", login);
        
        Response<UserDTO> response = new Response<>();
        if (login == null || login.isEmpty()) {
            response.getErrors().add("Informe um login");
            return ResponseEntity.badRequest().body(response);
        }
        
        UserDTO userDto;

        Optional<User> opt = userService.findByLogin(login);
        if (opt.isPresent()) {
            String avatar = userService.getAvatarBase64ByLogin(login);
            userDto = userConverter.entityToDto(opt.get());
            userDto.setAvatar(avatar);
            response.setData(userDto);
            return ResponseEntity.ok().body(response);
        }

        userDto = userService.buscarUsuarioApi(login);
        
        if (userDto == null) {
        	return ResponseEntity.notFound().build();
        }
                
        response.setData(userDto);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/all")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserDTO>>> getAll(@RequestBody Token token) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (token == null) { 
        	response.getErrors().add("Token precisa ser informado");
        }
                
        UserRestModel users = userService.carregarUsuariosApi(token.getToken());

        if (users == null) {
        	response.getErrors().add("Token inválido ou expirado!");
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (users.hasError()) {
        	users.getOutput().getMessages().forEach(e -> {
        		response.getErrors().add(e);
        	});
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (users.getOutput().getResult().getUsuarios().isEmpty()) {
            response.getErrors().add("Usuários não encontrados");
        }
        
        if (!response.getErrors().isEmpty()) {
        	return ResponseEntity.badRequest().body(response);        	
        }
        
        List<UserDTO> userList = new ArrayList<>();
        users.getOutput().getResult().getUsuarios().forEach(u -> {
        	userList.add(u.toUserDTO());
        });
        
        userList.forEach(u -> {
        	String avatarPath = userService.getAvatarDir(u.getLogin());
            if (avatarPath != null) {
            	String avatarBase64 = userService.getAvatarBase64(avatarPath);
            	u.setAvatar(avatarBase64);
            }
            
            String banner = userService.getBannerString(u.getLogin());
            u.setBanner(banner);
        });
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }
    
    
    @PostMapping(value = "/dropdown")
    @Transactional(timeout = 20000)
    public ResponseEntity<Response<List<UserReductDTO>>> getDropDown(@HeaderParam("x-access-token") Token token) {
    	
        log.info("Dropdown: ");
        log.info(token.getToken());
    	System.out.println(token.getToken());
    	
        Response<List<UserReductDTO>> response = new Response<>();
        if (token != null) {
            
            List<UserReductDTO> usuariosApiReduct = userService.carregarUsuariosApiReduct(token.getToken(), false); 

            if (usuariosApiReduct.isEmpty()) {   
                response.getErrors().add("Nenhum usuario encontrado");             
                return ResponseEntity.badRequest().body(response);
            }
            // usuariosApiReduct.forEach(u -> {
            //     u.setAvatar(null);
            // });
        	response.setData(usuariosApiReduct); 
        }
        return ResponseEntity.ok().body(response);
    }
    
    
    @PutMapping(value = "/permission")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserReductDTO>>> updatePermission(@RequestBody PermissionUserDTO permissionUser) {
    	
        Response<List<UserReductDTO>> response = new Response<>();

    	if (permissionUser.getLogin() == null || permissionUser.getLogin().isBlank()) {
            response.getErrors().add("Informe o login");
        } else {
            if (!userService.existsByLogin(permissionUser.getLogin())) {
                response.getErrors().add("Usuário não encontrado");
            }
        }

        if (permissionUser.getPermission() == null || permissionUser.getPermission().isBlank()) {
            response.getErrors().add("Informe a permissão");
        } else {
            if (permissionRepository.findByName(permissionUser.getPermission()) == null) {
                response.getErrors().add("Permissão não encontrada");
            }
        }
    	
        if (!response.getErrors().isEmpty()) {
            return ResponseEntity.badRequest().body(response);
        }

        
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/birthdays")
    @Transactional(timeout = 20000)
    public ResponseEntity<Response<List<UserDTO>>> getBirthDaysOfMonth(@HeaderParam("x-access-token") Token token) {
        
        Response<List<UserDTO>> response = new Response<>();

        UserRestModel users = userService.carregarUsuariosApi(token.getToken());

        if (users == null) {
    		response.getErrors().add("Nenhum usuario encontrado");    		
    		return ResponseEntity.badRequest().body(response);
    	}
        
        if (users.hasError()) {
        	users.getOutput().getMessages().forEach(u -> {
    			response.getErrors().add(u);
    		});
    		return ResponseEntity.badRequest().body(response);
        }
        
        List<UserDTO> aniversariantes = new ArrayList<UserDTO>();
        users.getOutput().getResult().getUsuarios().forEach(u -> {
        	if (u.getBirthDate() != null && UtilDataHora.isAniversariante(u.getBirthDate())) {
        		aniversariantes.add(u.toUserDTO());
        	}
        });
        
        if (aniversariantes.isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        aniversariantes.forEach(a -> {
            String avatar = userService.getAvatarBase64ByLogin(a.getLogin());
            a.setAvatar(avatar);
        });

        response.setData(aniversariantes);
        return ResponseEntity.ok().body(response);
    }

    
    @PutMapping()
    public ResponseEntity<Response<UserDTO>> put( @RequestBody UserRequestDTO dto,  BindingResult result ) {

        log.info("Alterando um usuário {}", dto);

        Response<UserDTO> response = new Response<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            response.getErrors().add("Login precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findByLogin(dto.getLogin());
        if (!optUser.isPresent()) {
            response.getErrors().add("Nenhum usuário encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        User user = optUser.get();
        user.setBanner(dto.getBanner());

        user = userService.persist(user);
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);        
    }


    @PutMapping("/atualizar-avatar/{login}")
    public ResponseEntity<Response<String>> atulizarAvatar( @PathVariable String login ) {

        log.info("Alterando avatar do usuário {}", login);

        Response<String> response = new Response<>();

        if (login == null || login.isBlank()) {
            response.getErrors().add("Login precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findByLogin(login);
        if (!optUser.isPresent()) {
            response.getErrors().add("Nenhum usuário encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        String caminhoArquivo = userService.gravarAvatarDiretorio(login);
        response.setData(caminhoArquivo);
        return ResponseEntity.ok().body(response);        
    }
    

    @GetMapping(value = "/search/{keyword}")
    @Transactional(timeout = 20000)
    public ResponseEntity<Response<List<UserDTO>>> search(@PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (keyword == null || keyword.isBlank()) {
            response.getErrors().add("Informe um termo");
            return ResponseEntity.badRequest().body(response);
        }
        
        List<UserDTO> userList = userService.search(keyword);
        userList.forEach(u -> {
            String avatar = userService.getAvatarBase64ByLogin(u.getLogin());
            if (avatar != null) {            	
            	u.setAvatar(avatar);
            }        	         
        });       
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }

    
    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
