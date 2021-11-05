package com.dminer.controllers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.constantes.Constantes;
import com.dminer.converters.UserConverter;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserRequestDTO;
import com.dminer.entities.FileInfo;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String USER_BANNER = "\\user\\banner";
    private static final String USER_AVATAR = "\\user\\avatar";

    @Autowired
    private UserService userService;

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private FileDatabaseService fileDatabaseService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ServerSendEvents serverSendEvents;
    

    private void validateRequestDto(UserRequestDTO userRequestDTO, BindingResult result) {
        if (userRequestDTO.getName() == null) {
            result.addError(new ObjectError("userRequestDTO", "Nome precisa estar preenchido."));			
		}
        if (userRequestDTO.getDtBirthday() == null) {
            result.addError(new ObjectError("userRequestDTO", "Data de aniversário precisa estar preenchido."));
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
    }
    
    private FileInfo salvarImagem(MultipartFile multipartFile, String directory, BindingResult result) {
        try {
            fileStorageService.createDirectory(Paths.get(directory));
            fileStorageService.save(multipartFile, Paths.get(directory));
        } catch (IOException e) {
            result.addError(new ObjectError("multipartFile", "Falha ao criar diretório para este usuário em: " + directory));
            log.info("Falha ao criar diretório para este usuário em: {} ", directory);
        }

        FileInfo info = new FileInfo();
        info.setUrl(directory + "\\" + multipartFile.getOriginalFilename());
        Optional<FileInfo> optinfo = fileDatabaseService.persist(info);
        if (!optinfo.isPresent()) {
            result.addError(new ObjectError("multipartFile", "Falha ao salvar arquivo para este usuário em: " + directory));
            log.info("Falha ao salvar arquivo para este usuário em: {} ", directory);
        }

        if (result.hasErrors()) return null;
        return optinfo.get();
    }



    @PostMapping(consumes = {"multipart/form-data", "multipart/form-data", "application/json"})
    public ResponseEntity<Response<UserDTO>> create( 
        @RequestPart(value = "avatar", required = false) MultipartFile avatar, 
        @RequestPart(value = "banner", required = false) MultipartFile banner, 
        @Valid @RequestPart("user") String userRequestJson, 
        BindingResult result
    ) {        

		log.info("Salvando um novo usuário {}", userRequestJson);

        Response<UserDTO> response = new Response<>();

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        try {
            ObjectMapper obj = new ObjectMapper();
            userRequestDTO = obj.readValue(userRequestJson, UserRequestDTO.class);
        } catch (IOException e) {
            response.getErrors().add("Erro ao converter objeto UserRequestDTO, verifique se a string está correta no formato Json!");
            return ResponseEntity.badRequest().body(response);
        }

        validateRequestDto(userRequestDTO, result);
        if (result.hasErrors()) {
            log.info("Erro validando userRequestDTO: {}", userRequestDTO);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        // salvando usuario para pegar o id
        User user = userService.persist(userConverter.requestDtoToEntity(userRequestDTO));
        
        if (avatar != null) {
            FileInfo file = salvarImagem(avatar, Constantes.ROOT_UPLOADS + USER_AVATAR + user.getId(), result);
            if (result.hasErrors()) {
                rollback(user);
                result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
                return ResponseEntity.internalServerError().body(response);
            }
            user.setAvatar(file);
        }
        
        if (banner != null) {
            FileInfo file = salvarImagem(banner, Constantes.ROOT_UPLOADS + USER_BANNER + user.getId(), result);
            if (result.hasErrors()) {
                rollback(user);
                result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
                return ResponseEntity.internalServerError().body(response);
            }
            user.setBanner(file);
        }        
        
        user = userService.persist(user);
        response.setData(userConverter.entityToDto(user));
        serverSendEvents.streamSseMvc(response.toString());
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<UserDTO>> putUser(
        @RequestPart(value = "avatar", required = false) MultipartFile avatar, 
        @RequestPart(value = "banner", required = false) MultipartFile banner, 
        @Valid @RequestPart("user") String userJson, 
        BindingResult result
    ) {

        log.info("Alterando um usuário {}", userJson);

        Response<UserDTO> response = new Response<>();

        UserDTO userDTO = new UserDTO();
        try {
            ObjectMapper obj = new ObjectMapper();
            userDTO = obj.readValue(userJson, UserDTO.class);
        } catch (IOException e) {
            response.getErrors().add("Erro ao converter objeto UserDTO, verifique se a string está correta no formato Json!");
            return ResponseEntity.badRequest().body(response);
        }

        validateDto(userDTO, result);
        if (result.hasErrors()) {
            log.info("Erro validando userDTO: {}", userDTO);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<User> optUser = userService.findById(userDTO.getId());
        optUser.get().setName(userDTO.getName());
        optUser.get().setDtBirthday(UtilDataHora.toTimestamp(userDTO.getDtBirthday()));

        if (avatar != null) {
            FileInfo file = salvarImagem(avatar, Constantes.ROOT_UPLOADS + USER_AVATAR + optUser.get().getId(), result);
            if (result.hasErrors()) {
                rollback(optUser.get());
                result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
                return ResponseEntity.internalServerError().body(response);
            }
            optUser.get().setAvatar(file);
        }
        
        if (banner != null) {
            FileInfo file = salvarImagem(banner, Constantes.ROOT_UPLOADS + USER_BANNER + optUser.get().getId(), result);
            if (result.hasErrors()) {
                rollback(optUser.get());
                result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
                return ResponseEntity.internalServerError().body(response);
            }
            optUser.get().setBanner(file);
        }

        User user = userService.persist(optUser.get());
        response.setData(userConverter.entityToDto(user));
        return ResponseEntity.ok().body(response);        
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<UserDTO>> getUser(@PathVariable("id") Integer id) {
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


    @GetMapping()
    public ResponseEntity<Response<List<UserDTO>>> getAllUser() {
        
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
    public ResponseEntity<Response<Boolean>> deleteUser(@PathVariable("id") Integer id) {
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


    @GetMapping("/birthdays/{month}")
    public ResponseEntity<Response<List<UserDTO>>> getBirthDaysOfMonth() {
        
        Response<List<UserDTO>> response = new Response<>();

        Optional<List<UserDTO>> user = userService.getBirthDaysOfMonth();
        if (user.get().isEmpty()) {
            response.getErrors().add("Nenhum aniversariante encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(user.get());
        return ResponseEntity.ok().body(response);
    }


    private void rollback(User user) {
        deleteUser(user.getId());
    }

}
