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
import com.dminer.dto.Token;
import com.dminer.dto.UserReductDTO;
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


    // @PostMapping(value = "/all")
    // public ResponseEntity<Response<List<PermissionDTO>>> getAll(@RequestBody Token token) {
        
    //     Response<List<PermissionDTO>> response = new Response<>();

    //     ConfigRestModel model = permissionService.carregarPermissoesApi(token.getToken());
    //     if (model == null || model.hasError()) {
    //         response.addError("Permissões não encontradas");
    //         model.getOutput().getMessages().forEach(u -> {
    // 			response.addError(u);
    // 		});
    //         return ResponseEntity.badRequest().body(response);
    //     }

    //     List<PermissionDTO> ps = new ArrayList<>();
    //     model.getOutput().getResult().getConfigs().forEach(p -> {
    //         ps.add(p.toPermissionDTO());
    //     });
    //     response.setData(ps);
    //     return ResponseEntity.ok().body(response);
    // }


    @PostMapping(value = "/dropdown")
    public ResponseEntity<Response<List<PermissionDTO>>> getDropDown() {
    	
    	Response<List<PermissionDTO>> response = new Response<>();

        List<Permission> permissions = permissionRepository.findAll();
        if (permissions == null) {
            response.addError("Permissões não encontradas");            
            return ResponseEntity.badRequest().body(response);
        }

        List<PermissionDTO> ps = new ArrayList<>();
        permissions.forEach(p -> {
            ps.add(permissionConverter.entityToDTO(p));
        });

        response.setData(ps);
        return ResponseEntity.ok().body(response);
    }
    
    // @PostMapping(value = "/dropdown")
    // public ResponseEntity<Response<List<PermissionReductDTO>>> getDropDown(@RequestBody Token token) {
    	
    // 	Response<List<PermissionReductDTO>> response = new Response<>();

    //     ConfigRestModel model = permissionService.carregarPermissoesApi(token.getToken());
    //     if (model == null || model.hasError()) {
    //         response.addError("Permissões não encontradas");
    //         model.getOutput().getMessages().forEach(u -> {
    // 			response.addError(u);
    // 		});
    //         return ResponseEntity.badRequest().body(response);
    //     }

    //     List<PermissionReductDTO> ps = new ArrayList<>();
    //     model.getOutput().getResult().getConfigs().forEach(p -> {
    //         ps.add(p.toPermissionReductDTO());
    //     });
    //     response.setData(ps);
    //     return ResponseEntity.ok().body(response);
    // }

}
