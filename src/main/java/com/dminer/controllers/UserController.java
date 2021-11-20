package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.PermissionConverter;
import com.dminer.converters.UserConverter;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.Permission;
import com.dminer.entities.User;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;
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

    private void validateRequestDto(UserRequestDTO userRequestDTO, BindingResult result) {
        if (userRequestDTO.getName() == null) {
            result.addError(new ObjectError("userRequestDTO", "Nome precisa estar preenchido."));			
		}
        if (userRequestDTO.getDtBirthday() == null) {
            result.addError(new ObjectError("userRequestDTO", "Data de aniversário precisa estar preenchido."));
		}
        if (userRequestDTO.getPermission() != null) {
            if (permissionRepository.existsById(userRequestDTO.getPermission()) == false) {
                result.addError(new ObjectError("userRequestDTO", "Permissão não cadastrada."));
            }
        }
    }

    private void validateDto(UserDTO userDTO, BindingResult result) {
        if (userDTO.getId() == null) {
            result.addError(new ObjectError("userDTO", "Id precisa estar preenchido."));
		}
        if (userDTO.getName() == null) {
            result.addError(new ObjectError("userDTO", "Nome precisa estar preenchido."));			
		}
        if (userDTO.getDtBirthday() == null) {
            result.addError(new ObjectError("userDTO", "Data de aniversário precisa estar preenchido."));
		}
        if (userDTO.getId() != null) {
            Optional<User> optUser = userService.findById(userDTO.getId());
            if (!optUser.isPresent()) {
                log.info("Usuário não encontrado: {}", userDTO);
                result.addError(new ObjectError("userDTO", "Usuário não encontrado."));
            }
		}
        if (userDTO.getPermission() != null) {
            Optional<Permission> opt = permissionRepository.findById(userDTO.getPermission().getId());
            if (!opt.isPresent()) {
                log.info("Permissão não encontrada: {}", userDTO.getPermission().toString());
                result.addError(new ObjectError("userDTO", "Permissão não encontrada."));
            }
		}
    }


    @PostMapping()
    public ResponseEntity<Response<UserDTO>> create(@Valid @RequestBody UserRequestDTO userRequestDto, BindingResult result) {        

		log.info("Salvando um novo usuário {}", userRequestDto.getName());

        Response<UserDTO> response = new Response<>();

        validateRequestDto(userRequestDto, result);
        if (result.hasErrors()) {
            log.info("Erro validando userRequestDTO: {}", userRequestDto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        User u = userConverter.requestDtoToEntity(userRequestDto);

        User user = userService.persist(u);
        
        System.out.println("\n\n");
        System.out.println(userRequestDto.toString());
        System.out.println(u.toString());
        System.out.println(user.toString());
        System.out.println("\n\n");


        response.setData(userConverter.entityToDto(user));
        // serverSendEvents.streamSseMvc(response.toString());
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<UserDTO>> put( @Valid @RequestBody UserDTO userDto, BindingResult result) {

        log.info("Alterando um usuário {}", userDto);

        Response<UserDTO> response = new Response<>();

        validateDto(userDto, result);
        if (result.hasErrors()) {
            log.info("Erro validando UserRequestDTO: {}", userDto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findById(userDto.getId());
        optUser.get().setName(userDto.getName());
        optUser.get().setDtBirthday(UtilDataHora.toTimestamp(userDto.getDtBirthday()));
        optUser.get().setAvatar(userDto.getAvatar());
        optUser.get().setBanner(userDto.getBanner());
        optUser.get().setEmail(userDto.getEmail());
        optUser.get().setArea(userDto.getArea());
        optUser.get().setLinkedin(userDto.getLinkedin());
        optUser.get().setNickname(userDto.getNickname());
        if (userDto.getPermission() != null)
            optUser.get().setPermission(new PermissionConverter().dtoToEntity(userDto.getPermission()));

        User user = userService.persist(optUser.get());
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<UserDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando usuário {}", id);
        
        Response<UserDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> user = userService.findById(id);
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
