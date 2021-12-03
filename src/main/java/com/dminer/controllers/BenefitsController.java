package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.BenefitsConverter;
import com.dminer.dto.BenefitsRequestDTO;
import com.dminer.dto.BenefitsDTO;
import com.dminer.entities.Benefits;
import com.dminer.entities.User;
import com.dminer.repository.BenefitsRepository;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.PermissionRepository;
import com.dminer.repository.UserRepository;
import com.dminer.response.Response;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

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

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/benefits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BenefitsController {
    
    private static final Logger log = LoggerFactory.getLogger(BenefitsController.class);

    @Autowired 
    private BenefitsConverter benefitsConverter;

    @Autowired
    private BenefitsRepository benefitsRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GenericRepositoryPostgres genericRepositoryPostgres;

    @Autowired
    private GenericRepositorySqlServer genericRepositorySqlServer;

    @Autowired
    private Environment env;

    

    private void validateRequestDto(BenefitsRequestDTO dto, BindingResult result) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            result.addError(new ObjectError("BenefitsRequestDTO", "Titulo precisa estar preenchido."));
        }

        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            result.addError(new ObjectError("BenefitsRequestDTO", "Conteúdo precisa estar preenchido."));
        }

        if (dto.getPermission() == null) {
            result.addError(new ObjectError("BenefitsRequestDTO", "Permissão precisa estar preenchido."));
        } else {
            if(!permissionRepository.existsById(dto.getPermission())) {
                result.addError(new ObjectError("BenefitsRequestDTO", "Permissão não é válida."));
            }
        }
        
        if (dto.getCreator() == null) {
            result.addError(new ObjectError("dto", "Responsável precisa estar preenchido."));
        } else {
        	String login = dto.getCreator();
            if (!userService.existsByLogin(login)) {
                result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
            }
        } 

        if (dto.getDate() == null || dto.getDate().isEmpty()) {
            result.addError(new ObjectError("BenefitsRequestDTO", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        }
       
        if (dto.getDate() == null || dto.getDate().isEmpty()) {
            result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        } else {
        	if (!UtilDataHora.isTimestampValid(dto.getDate())) {
        		result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        	}
        }
    }
    
   
    private void validateDto(BenefitsDTO dto, BindingResult result) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            result.addError(new ObjectError("dto", "Title precisa estar preenchido."));
        }

        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            result.addError(new ObjectError("dto", "Conteúdo precisa estar preenchido."));
        }

        if (dto.getId() == null) {
            result.addError(new ObjectError("dto", "Id do benefício precisa estar preenchido."));
        } else {
            if(!benefitsRepository.existsById(dto.getId())) {
                result.addError(new ObjectError("dto", "Id do benefício não é válida."));
            }
        }

        if (dto.getPermission() == null) {
            result.addError(new ObjectError("dto", "Permissão precisa estar preenchido."));
        } else {
            if(!permissionRepository.existsById(dto.getPermission())) {
                result.addError(new ObjectError("dto", "Permissão não é válida."));
            }
        }
        
        if (dto.getCreator() == null) {
            result.addError(new ObjectError("dto", "Responsável precisa estar preenchido."));
        } else {
        	String login = dto.getCreator();
            if (!userService.existsByLogin(login)) {
                result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
            }
        }

        if (dto.getDate() == null || dto.getDate().isEmpty()) {
            result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        } else {
        	if (!UtilDataHora.isTimestampValid(dto.getDate())) {
        		result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        	}
        }       
    }


    @PostMapping
    public ResponseEntity<Response<BenefitsDTO>> create(@Valid @RequestBody BenefitsRequestDTO dto, BindingResult result) {
    
		Response<BenefitsDTO> response = new Response<>();
        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Benefits doc = benefitsRepository.save(benefitsConverter.requestDtoToEntity(dto));
        response.setData(benefitsConverter.entityToDTO(doc));

        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<BenefitsDTO>> put( @Valid @RequestBody BenefitsDTO dto, BindingResult result) {

        log.info("Alterando um categoria {}", dto);

        Response<BenefitsDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Benefits benefits = benefitsRepository.save(benefitsConverter.dtoToEntity(dto));
        response.setData(benefitsConverter.entityToDTO(benefits));
        return ResponseEntity.ok().body(response);
    }
    
    
    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<BenefitsDTO>> get(@PathVariable("id") Integer id) {
        
        Response<BenefitsDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Benefits> doc = benefitsRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(benefitsConverter.entityToDTO(doc.get()));
        return ResponseEntity.ok().body(response);
    }

//    @GetMapping(value = "/seacrh/{search}")
//    public ResponseEntity<Response<BenefitsDTO>> search(@PathVariable("search") String search) {
//        
//        Response<BenefitsDTO> response = new Response<>();
//        if (search == null) {
//            response.getErrors().add("Informe algo para pesquisar");
//            return ResponseEntity.badRequest().body(response);
//        }
//
//        List<Benefits> search2 = new ArrayList<>();
//
//        if (isProd()) {
//            search2 = genericRepositoryPostgres.searchBenefits(search);            
//        } else {
//            search2 = genericRepositorySqlServer.searchBenefits(search);
//        }
//
//        if (!search2.isEmpty()) {
//            response.getErrors().add("Nenhum dado encontrado");
//            return ResponseEntity.status(404).body(response);
//        }
//        
//        search2.forEach(s -> {
//            response.setData(benefitsConverter.entityToDTO(s));
//        });
//        return ResponseEntity.ok().body(response);
//    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<BenefitsDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<BenefitsDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Benefits> doc = benefitsRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        try {benefitsRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(benefitsConverter.entityToDTO(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<BenefitsDTO>>> getAll() {
        
        Response<List<BenefitsDTO>> response = new Response<>();

        List<Benefits> doc = benefitsRepository.findAll();
        if (doc.isEmpty()) {
            response.getErrors().add("Beneficios não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<BenefitsDTO> eventos = new ArrayList<>();
        doc.forEach(u -> {        	
            eventos.add(benefitsConverter.entityToDTO(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
