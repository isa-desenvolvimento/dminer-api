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

    private User criarNovoUser(String login) {
        User user = new User();
        user.setLogin(login);
        if (userService.existsByLogin(login) == false) {
            return userService.persist(user);
        }
        return user;
    }

    @GetMapping(value = "/{login}")
    public ResponseEntity<Response<UserDTO>> get(@RequestHeader("x-access-token") Token token, @PathVariable("login") String login) {
        log.info("Buscando usuário {}", login);
        
        Response<UserDTO> response = new Response<>();
        if (login == null || login.isEmpty()) {
            response.addError("Informe um login");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        UserDTO userDto;

        criarNovoUser(login);

        Optional<User> opt = userService.findByLogin(login);
        if (opt.isPresent()) {
            String avatar = userService.getAvatarBase64ByLogin(login);
            userDto = userConverter.entityToDto(opt.get());
            userDto.setAvatar(avatar);
            response.setData(userDto);
            return ResponseEntity.ok().body(response);
        } 

        userDto = userService.buscarUsuarioApi(login, token.getToken());
        
        if (userDto == null) {
        	return ResponseEntity.notFound().build();
        }
                
        response.setData(userDto);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all")
    @Transactional(timeout = 999999)
    public ResponseEntity<Response<List<UserDTO>>> getAll(@RequestHeader("x-access-token") Token token) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (token.naoPreenchido()) { 
        	response.addError("Token precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }
                
        UserRestModel users = userService.carregarUsuariosApi(token.getToken());

        if (users == null) {
        	response.addError("Token inválido ou expirado!");
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (users.hasError()) {
        	users.getOutput().getMessages().forEach(e -> {
        		response.addError(e);
        	});
        	return ResponseEntity.badRequest().body(response);
        }
        
        if (users.getOutput().getResult().getUsuarios().isEmpty()) {
            response.addError("Usuários não encontrados");
        }
        
        if (response.containErrors()) {
        	return ResponseEntity.badRequest().body(response);        	
        }
        
        System.out.println("\n\nRecuperou os usuarios!!! \n");
        List<UserDTO> userList = new ArrayList<>();
        users.getUsers().forEach(u -> {
            UserDTO userDto = u.toUserDTO();
            // String avatarBase64 = userService.getAvatarEndpointEGravaDiretorio(u.getLogin());        	
            // if (avatarBase64 != null) {
            //     userDto.setAvatar(avatarBase64);
            // }
            userDto.setAvatar(userService.getAvatarBase64ByLogin(u.getLogin()));
            String banner = userService.getBannerString(u.getLogin());
            userDto.setBanner(banner);
            System.out.println(userDto.toJson());
        	userList.add(userDto);
        });
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }
    
    
    @PostMapping(value = "/dropdown")
    @Transactional(timeout = 99999)
    public ResponseEntity<Response<List<UserReductDTO>>> getDropDown(@RequestHeader("x-access-token") Token token) {
    	
        Response<List<UserReductDTO>> response = new Response<>();
        if (token.naoPreenchido()) { 
        	response.addError("Token precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }
    
        // List<UserReductDTO> usuariosApiReduct = userService.carregarUsuariosApiReduct(token.getToken(), false);
        // UserRestModel restModel = userService.carregarUsuariosApi(token.getToken());

        // if (restModel.isEmptyUsers()) {
        //     response.addError("Nenhum usuario encontrado");             
        //     return ResponseEntity.badRequest().body(response);
        // }
        
        // response.setData(restModel.toUserReductDtoList()); 
    
        List<UserReductDTO> carregarUsuariosApiReduct = userService.carregarUsuariosApiReduct(token.getToken(), false);
        if (carregarUsuariosApiReduct.isEmpty()) {
            response.addError("Nenhum usuario encontrado");             
            return ResponseEntity.ok().body(response);
        }

        carregarUsuariosApiReduct.forEach(user -> {
            criarNovoUser(user.getLogin());
        });

        response.setData(carregarUsuariosApiReduct); 
        return ResponseEntity.ok().body(response);
    }
    
    
    @PutMapping(value = "/permission")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserReductDTO>>> updatePermission(@RequestBody PermissionUserDTO permissionUser) {
    	
        Response<List<UserReductDTO>> response = new Response<>();

    	if (permissionUser.getLogin() == null || permissionUser.getLogin().isBlank()) {
            response.addError("Informe o login");
        } else {
            if (!userService.existsByLogin(permissionUser.getLogin())) {
                response.addError("Usuário não encontrado");
            }
        }

        if (permissionUser.getPermission() == null || permissionUser.getPermission().isBlank()) {
            response.addError("Informe a permissão");
        } else {
            if (permissionRepository.findByName(permissionUser.getPermission()) == null) {
                response.addError("Permissão não encontrada");
            }
        }
    	
        if (response.containErrors()) {
            return ResponseEntity.badRequest().body(response);
        }

        
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/birthdays")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserDTO>>> getBirthDaysOfMonth(@RequestHeader("x-access-token") Token token) {
        
        Response<List<UserDTO>> response = new Response<>();

        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        UserRestModel users = userService.carregarUsuariosApi(token.getToken());

        if (users == null) {
    		response.addError("Nenhum usuario encontrado");    		
    		return ResponseEntity.badRequest().body(response);
    	}
        
        if (users.hasError()) {
        	users.getOutput().getMessages().forEach(u -> {
    			response.addError(u);
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
            response.addError("Nenhum aniversariante encontrado");
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
            response.addError("Login precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findByLogin(dto.getLogin());
        if (!optUser.isPresent()) {
            response.addError("Nenhum usuário encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        User user = optUser.get();
        user.setBanner(dto.getBanner());

        user = userService.persist(user);
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);        
    }


    @PutMapping("/persist")
    public ResponseEntity<Response<UserDTO>> persist( @RequestBody UserRequestDTO dto,  BindingResult result ) {

        log.info("Alterando/salvando um usuário {}", dto);

        Response<UserDTO> response = new Response<>();

        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            response.addError("Login precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        User user = null;
        Optional<User> optUser = userService.findByLogin(dto.getLogin());
        if (!optUser.isPresent()) {
            user = new User();
            user.setLogin(dto.getLogin());
        } else {
            user = optUser.get();
        }

        log.info("Alterando/salvando um usuário {}", dto);

        user = userService.persist(user);
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);        
    }


    @PutMapping("/atualizar-avatar/{login}")
    public ResponseEntity<Response<String>> atualizarAvatar( @PathVariable String login ) {

        log.info("Alterando avatar do usuário {}", login);

        Response<String> response = new Response<>();

        if (login == null || login.isBlank()) {
            response.addError("Login precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findByLogin(login);
        if (!optUser.isPresent()) {
            response.addError("Nenhum usuário encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        String caminhoArquivo = userService.gravarAvatarDiretorio(login);
        response.setData(caminhoArquivo);
        return ResponseEntity.ok().body(response);        
    }
    
    // perguntar se ainda vai usar este endpoint
    @GetMapping(value = "/search/{keyword}")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (keyword == null || keyword.isBlank()) {
            response.addError("Informe um termo");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        List<UserDTO> userList = userService.search(keyword, token.getToken());
        userList.forEach(u -> {
        	String avatarPath = userService.getAvatarDir(u.getLogin());            
            if (avatarPath != null) {
            	String avatarBase64 = userService.getAvatarBase64(avatarPath);
            	u.setAvatar(avatarBase64);
            }            
        });       
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/search/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<List<UserDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();
        if (keyword == null || keyword.isBlank()) {
            response.addError("Informe um termo");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        List<UserDTO> userList = userService.search(keyword, token.getToken());
        userList.forEach(u -> {
        	String avatarPath = userService.getAvatarDir(u.getLogin());            
            if (avatarPath != null) {
            	String avatarBase64 = userService.getAvatarBase64(avatarPath);
            	u.setAvatar(avatarBase64);
            }            
        });       
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/inserirDadosNoBancoComApiExterna")
    public ResponseEntity<Boolean> inserirDadosNoBancoComApiExterna() {        
        userService.inserirDadosNoBancoComApiExterna();
        return ResponseEntity.ok().build();
    }

    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
