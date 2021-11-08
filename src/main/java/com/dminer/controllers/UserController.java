package com.dminer.controllers;

import java.io.IOException;
import java.nio.file.Paths;
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
import org.springframework.web.multipart.MultipartFile;

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
    private FileDatabaseService fileDatabaseService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ServerSendEvents serverSendEvents;
    
    @Autowired
    private Environment env;

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



    @PostMapping()
    public ResponseEntity<Response<UserDTO>> create(@Valid @RequestBody UserRequestDTO userRequestDto, BindingResult result) {        

		log.info("Salvando um novo usuário {}", userRequestDto.getName());

        Response<UserDTO> response = new Response<>();

        UserRequestDTO userRequestDTO = new UserRequestDTO();

        validateRequestDto(userRequestDTO, result);
        if (result.hasErrors()) {
            log.info("Erro validando userRequestDTO: {}", userRequestDTO);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.persist(userConverter.requestDtoToEntity(userRequestDTO));
        
        response.setData(userConverter.entityToDto(user));
        serverSendEvents.streamSseMvc(response.toString());
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
