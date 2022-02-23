package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dminer.converters.NoticeConverter;
import com.dminer.dto.NoticeDTO;
import com.dminer.dto.NoticeRequestDTO;
import com.dminer.entities.Notice;
import com.dminer.entities.User;
import com.dminer.response.Response;
import com.dminer.services.NoticeService;
import com.dminer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/notice")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NoticeController {
    
    private static final Logger log = LoggerFactory.getLogger(NoticeController.class);

    @Autowired 
    private NoticeConverter noticeConverter;

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;
    
    @Autowired
    private Environment env;


    @PostMapping
    public ResponseEntity<Response<NoticeDTO>> create(@Valid @RequestBody NoticeRequestDTO avisosRequest, BindingResult result) {
    
        log.info("Salvando um novo aviso {}", avisosRequest);

		Response<NoticeDTO> response = new Response<>();
        //validateRequestDto(avisosRequest, result);
        if (result.hasErrors()) {
            log.info("Erro validando NoticeDTO: {}", avisosRequest);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Notice events = noticeService.persist(noticeConverter.requestDtoToEntity(avisosRequest));
        response.setData(noticeConverter.entityToDTO(events));

        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<NoticeDTO>> put( @Valid @RequestBody NoticeDTO dto, BindingResult result) {

        log.info("Alterando um notice {}", dto);

        Response<NoticeDTO> response = new Response<>();

        //validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando NoticeDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Notice notice = noticeService.persist(noticeConverter.dtoToEntity(dto));
        response.setData(noticeConverter.entityToDTO(notice));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<NoticeDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando avisos {}", id);
        
        Response<NoticeDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Notice> user = noticeService.findById(id);
        if (!user.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.ok().body(response);
        }

        response.setData(noticeConverter.entityToDTO(user.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<NoticeDTO>> delete(@PathVariable("id") Integer id) {
        log.info("Buscando avisos {}", id);
        
        Response<NoticeDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Notice> not = noticeService.findById(id);
        if (!not.isPresent()) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.ok().body(response);
        }

        try {noticeService.delete(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Notificação não encontrada");
            return ResponseEntity.ok().body(response);
        }

        response.setData(noticeConverter.entityToDTO(not.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<NoticeDTO>>> getAll() {
        
        Response<List<NoticeDTO>> response = new Response<>();

        Optional<List<Notice>> userOpt = noticeService.findAll();
        if (userOpt.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.ok().body(response);
        }

        List<Notice> user = userOpt.get();
        
        user = user.stream()
		.sorted(Comparator.comparing(Notice::getDate).reversed())
		.collect(Collectors.toList());

        List<NoticeDTO> eventos = new ArrayList<>();
        user.forEach(u -> {
            eventos.add(noticeConverter.entityToDTO(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    //@PathVariable("login") String login
    @GetMapping("/all/{login}")
    public ResponseEntity<Response<List<NoticeDTO>>> getAll(@PathVariable("login") String login) {
        
        Response<List<NoticeDTO>> response = new Response<>();

        Optional<List<Notice>> noticeOpt = noticeService.findAll();
        if (noticeOpt.get().isEmpty()) {
            response.getErrors().add("Eventos não encontrados");
            return ResponseEntity.ok().body(response);
        }

        List<Notice> notice = noticeOpt.get();
        
        notice = notice.stream()
        .filter(u -> u.getCreator().equals(login))
        .collect(Collectors.toList());

        notice = notice.stream()
		.sorted(Comparator.comparing(Notice::getDate).reversed())
		.collect(Collectors.toList());

        List<NoticeDTO> eventos = new ArrayList<>();
        notice.forEach(u -> {
            eventos.add(noticeConverter.entityToDTO(u));
        });
        
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/search/{login}/{keyword}")
    @Transactional(timeout = 90000)
    public ResponseEntity<Response<List<NoticeDTO>>> search(@RequestHeader("x-access-token") Token token, @PathVariable String login, @PathVariable String keyword) {
        
        Response<List<NoticeDTO>> response = new Response<>();

        List<Notice> search = noticeService.search(keyword, isProd());
        search.forEach(notice -> {
            NoticeDTO dto = noticeConverter.entityToDTO(notice);
            response.getData().add(dto); 
        });
        
        return ResponseEntity.ok().body(response);
    }


    
    private void validateRequestDto(NoticeRequestDTO avisosRequestDTO, BindingResult result) {
        if (avisosRequestDTO.getUsers() == null) {
            result.addError(new ObjectError("NoticeRequestDTO", "Id do usuário precisa estar preenchido."));
		} else {
            avisosRequestDTO.getUsers().forEach(login -> {
            	Optional<User> findById = userService.findByLogin(login);
                if (!findById.isPresent()) {
                    result.addError(new ObjectError("NoticeRequestDTO", "Usuário: " + login + " não encontrado."));
                }
            });
        }

        if (avisosRequestDTO.getPriority() == null) {
            result.addError(new ObjectError("NoticeRequestDTO", "Prioridade do aviso precisa estar preenchido."));			
		}

        if (avisosRequestDTO.getCreator() == null || avisosRequestDTO.getCreator().isEmpty()) {
            result.addError(new ObjectError("NoticeRequestDTO", "Criador do aviso precisa estar preenchido."));			
		}

        if (avisosRequestDTO.getDate()== null || avisosRequestDTO.getDate().isEmpty()) {
            result.addError(new ObjectError("NoticeRequestDTO", "Data do aviso precisa estar preenchido."));			
		}       
    }


    private void validateDto(NoticeDTO dto, BindingResult result) {
        if (dto.getUsers() == null) {
            result.addError(new ObjectError("NoticeRequestDTO", "Id do usuário precisa estar preenchido."));
		} else {
            dto.getUsers().forEach(login -> {
                Optional<User> findById = userService.findByLogin(login.getLogin());
                if (!findById.isPresent()) {
                    result.addError(new ObjectError("NoticeRequestDTO", "Usuário: "+ login.getLogin() +" não encontrado."));
                }
            });
        }

        if (dto.getPriority() == null) {
            result.addError(new ObjectError("NoticeRequestDTO", "Prioridade do aviso precisa estar preenchido."));			
		}

        if (dto.getCreator() == null || dto.getCreator().isEmpty()) {
            result.addError(new ObjectError("NoticeRequestDTO", "Criador do aviso precisa estar preenchido."));			
		}

        if (dto.getDate()== null || dto.getDate().isEmpty()) {
            result.addError(new ObjectError("NoticeRequestDTO", "Data do aviso precisa estar preenchido."));			
		}

        if (dto.getId() == null) {
            result.addError(new ObjectError("NoticeRequestDTO", "Id precisa estar preenchido."));
		}
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
    

}
