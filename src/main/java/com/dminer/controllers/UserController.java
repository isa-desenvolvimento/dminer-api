package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.UserConverter;
import com.dminer.dto.PermissionUserDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;
import com.dminer.rest.DminerWebService;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.rest.model.users.Usuario;
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

    // @Autowired
    // private DminerWebService dminerWebService;

    @Autowired
    private Environment env;


    private User criarNovoUser(String login, String userName, String avatar) {
        // log.info("criando novo user {}, {}", login, userName);
        
        User user = new User();
        user.setLogin(login);
        user.setUserName(userName);
        if (userService.existsByLoginAndUserName(login, userName)) {
            user = userService.findByLogin(login).get();
            if (avatar != null && !avatar.isBlank())
                user.setAvatar(avatar);
        } 
        if (userService.existsByLogin(login)) {
            user = userService.findByLogin(login).get();
            user.setUserName(userName);
            if (avatar != null && !avatar.isBlank())
                user.setAvatar(avatar);
        }
        return userService.persist(user);
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

        boolean jaExisteNoBanco = false;
        UserDTO userDto = userService.buscarUsuarioApi(login, token.getToken());
        if (userDto == null) {
            jaExisteNoBanco = userService.existsByLogin(login);
        }

        if (jaExisteNoBanco) {
            Optional<User> opt = userService.findByLogin(login);
            if (opt.isPresent()) {
                userDto = userConverter.entityToDto(opt.get());
                response.setData(userDto);
                return ResponseEntity.ok().body(response);
            } 
        }
        
        criarNovoUser(login, userDto.getUserName(), userDto.getAvatar());

        
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
        
        List<UserDTO> usersDto = new ArrayList<>();

        UserRestModel<Usuario> users = null;
        if (DminerWebService.getInstance().jaProcessouUsuarios() == false) {

            Optional<List<User>> findAll = userService.findAll();
            if (findAll.isPresent()) {
                log.info("Recuperando usuários getAll direto no banco");
                findAll.get().forEach(user -> {
                    UserDTO dto = user.convertDto();
                    usersDto.add(dto);
                });
                response.setData(usersDto);
                return ResponseEntity.ok().body(response);
            }
        } else {
            users = DminerWebService.getInstance().getUsuariosApi(token.getToken());
        }

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

        for (Usuario usuario : users.getUsuarios()) {            
            UserDTO dto = usuario.toUserDTO(true);
            dto.setBanner(userService.getBannerByLogin(usuario.getLogin()));
            usersDto.add(dto);
        }

        response.setData(usersDto);
        return ResponseEntity.ok().body(response);
    }
    
    
    /**
     * Requisitar uma lista de usuários com os seguintes atributos: {login, username}
     * @param token
     * @return Lista de usuários sem o avatar
     */
    @PostMapping(value = "/dropdown")
    @Transactional(timeout = 99999)
    public ResponseEntity<Response<List<UserReductDTO>>> getDropDown(@RequestHeader("x-access-token") Token token) {
    	
        Response<List<UserReductDTO>> response = new Response<>();
        if (token.naoPreenchido()) { 
        	response.addError("Token precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

        if (DminerWebService.getInstance().jaProcessouUsuarios() == false) {

            Optional<List<User>> findAll = userService.findAll();
            if (findAll.isPresent()) {
                log.info("Recuperando usuários dropdown direto no banco");
                List<UserReductDTO> usersReduct = new ArrayList<>();
                findAll.get().forEach(user -> {
                    UserReductDTO dto = user.convertReductDto();
                    usersReduct.add(dto);
                });
                response.setData(usersReduct);
                return ResponseEntity.ok().body(response);
            }
        }  

        List<UserReductDTO> carregarUsuariosApiReduct = userService.carregarUsuariosApiReductDto(token.getToken(), true);
        if (carregarUsuariosApiReduct.isEmpty()) {
            response.addError("Nenhum usuario encontrado em getDropDown");             
            return ResponseEntity.ok().body(response);
        }

        carregarUsuariosApiReduct.forEach(user -> {
            criarNovoUser(user.getLogin(), user.getUserName(), user.getAvatar());
            user.setAvatar(null);
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

        List<UserDTO> users = userService.getAniversariantes(token.getToken(), true);

        if (users == null) {
    		response.addError("Não foi possível buscar os aniversariantes! Verifique se o token é válido.");
    		return ResponseEntity.badRequest().body(response);
    	}
        
        if (users.isEmpty()) {
    		response.addError("Nenhum usuario encontrado");
    		return ResponseEntity.ok().body(response);
    	}

        response.setData(users);
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


    // perguntar se ainda vai usar este endpoint
    @GetMapping(value = "/search/{keyword}")
    @Transactional(timeout = 10000)
    public ResponseEntity<Response<List<UserDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();
        // if (keyword == null || keyword.isBlank()) {
        //     response.addError("Informe um termo");
        //     return ResponseEntity.badRequest().body(response);
        // }
        
        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        if (keyword.equalsIgnoreCase("null")) keyword = null;

        List<UserDTO> userList = userService.search(keyword, token.getToken());
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/search/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<List<UserDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<List<UserDTO>> response = new Response<>();

        if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

        if (keyword.equalsIgnoreCase("null")) keyword = null;
        
        List<UserDTO> userList = userService.search(keyword, token.getToken());
        
        response.setData(userList);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
