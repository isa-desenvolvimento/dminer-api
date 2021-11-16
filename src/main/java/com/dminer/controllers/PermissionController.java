package com.dminer.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.converters.PermissionConverter;
import com.dminer.dto.PermissionDTO;
import com.dminer.dto.PermissionRequestDTO;
import com.dminer.entities.Permission;
import com.dminer.repository.PermissionRepository;
import com.dminer.response.Response;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/permission")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionConverter permissionConverter;


    private void validateRequestDto(PermissionRequestDTO dto, BindingResult result) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty())  {
            result.addError(new ObjectError("dto", "Permissão precisa estar preenchido."));			
		}
    }

    private void validateDto(PermissionDTO dto, BindingResult result) {
        if (dto.getId() == null)  {
            result.addError(new ObjectError("dto", "Id precisa estar preenchido."));			
		}

        if (dto.getTitle() == null || dto.getTitle().isEmpty())  {
            result.addError(new ObjectError("dto", "Permissão precisa estar preenchido."));			
		}
    }
    
    @PostMapping()
    public ResponseEntity<Response<PermissionDTO>> create(@Valid @RequestBody PermissionRequestDTO dto, BindingResult result) {        

		log.info("Salvando uma nova permissão {}", dto.getTitle());

        Response<PermissionDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Permission permission = permissionRepository.save(permissionConverter.requestDtoToEntity(dto));
        
        response.setData(permissionConverter.entityToDTO(permission));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<PermissionDTO>> put( @Valid @RequestBody PermissionDTO dto, BindingResult result) {

        log.info("Alterando uma permissão {}", dto);

        Response<PermissionDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando PermissionRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Permission> optUser = permissionRepository.findById(dto.getId());
        if (! optUser.isPresent()) {
            log.info("Permissão não encontrada: {}", dto);
            response.getErrors().add("Permissão não encontrada");
            return ResponseEntity.badRequest().body(response);
        }

        optUser.get().setPermission(dto.getTitle());

        Permission permission = permissionRepository.save(optUser.get());
        response.setData(permissionConverter.entityToDTO(permission));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<PermissionDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando usuário {}", id);
        
        Response<PermissionDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Permission> permission = permissionRepository.findById(id);
        if (!permission.isPresent()) {
            response.getErrors().add("Permissão não encontrada");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(permissionConverter.entityToDTO(permission.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<Response<List<PermissionDTO>>> getAll() {
        
        Response<List<PermissionDTO>> response = new Response<>();

        List<Permission> permission = permissionRepository.findAll();
        if (permission == null || permission.isEmpty()) {
            response.getErrors().add("Permissões não encontradas");
            return ResponseEntity.badRequest().body(response);
        }

        List<PermissionDTO> ps = new ArrayList<>();
        permission.forEach(p -> {
            ps.add(permissionConverter.entityToDTO(p));
        });
        response.setData(ps);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        log.info("Deletando permissão {}", id);
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {permissionRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Permissão não encontrada");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


}
