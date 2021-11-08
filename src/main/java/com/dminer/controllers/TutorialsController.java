package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.TutorialsConverter;
import com.dminer.converters.NoticeConverter;
import com.dminer.dto.TutorialsRequestDTO;
import com.dminer.dto.TutorialsRequestDTO;
import com.dminer.dto.TutorialsDTO;
import com.dminer.dto.NoticeRequestDTO;
import com.dminer.entities.Tutorials;
import com.dminer.entities.Tutorials;
import com.dminer.entities.Notice;
import com.dminer.entities.User;
import com.dminer.enums.Category;
import com.dminer.enums.Permissions;
import com.dminer.enums.Profiles;
import com.dminer.repository.TutorialsRepository;
import com.dminer.repository.TutorialsRepositoryPostgres;
import com.dminer.repository.TutorialsRepositorySqlServer;
import com.dminer.response.Response;
import com.dminer.repository.BenefitsRepositoryPostgres;
import com.dminer.services.NoticeService;
import com.dminer.services.UserService;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tutorials")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TutorialsController {
    
    private static final Logger log = LoggerFactory.getLogger(TutorialsController.class);

    @Autowired 
    private TutorialsConverter tutorialsConverter;

    @Autowired
    private TutorialsRepository tutorialsRepository;

    @Autowired
    TutorialsRepositorySqlServer tutorialsRepositorySqlServer;

    @Autowired
    TutorialsRepositoryPostgres tutorialsRepositoryPostgres;

    @Autowired
    private Environment env;

    Profiles profile;

    private void validateRequestDto(TutorialsRequestDTO dto, BindingResult result) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            result.addError(new ObjectError("TutorialsRequestDTO", "Titulo precisa estar preenchido."));
        }

        if (dto.getContent() == null || dto.getContent().isEmpty()) {
            result.addError(new ObjectError("TutorialsRequestDTO", "Conteúdo precisa estar preenchido."));
        }

        if (dto.getProfiles() == null || dto.getProfiles().isEmpty()) {
            result.addError(new ObjectError("TutorialsRequestDTO", "Perfil precisa estar preenchido."));
        } else {
            Arrays.asList(Profiles.values()).forEach(c -> {
                if (c.name().equals(dto.getProfiles())) {
                    profile = Profiles.valueOf(dto.getProfiles());
                    return;
                }
            });

            if (profile == null) {
                result.addError(new ObjectError("TutorialsRequestDTO", "Perfil não é válida."));
            }
        }

        if (dto.getDate() == null || dto.getTitle().isEmpty()) {
            result.addError(new ObjectError("TutorialsRequestDTO", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss"));
        }
       
    }
    
   

    @PostMapping
    public ResponseEntity<Response<TutorialsDTO>> create(@Valid @RequestBody TutorialsRequestDTO dto, BindingResult result) {
    
		Response<TutorialsDTO> response = new Response<>();
        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Tutorials doc = tutorialsRepository.save(tutorialsConverter.requestDtoToEntity(dto));
        response.setData(tutorialsConverter.entityToDTO(doc));

        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<TutorialsDTO>> get(@PathVariable("id") Integer id) {
        
        Response<TutorialsDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Tutorials> doc = tutorialsRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(tutorialsConverter.entityToDTO(doc.get()));
        return ResponseEntity.ok().body(response);
    }

    @GetMapping(value = "/seacrh/{search}")
    public ResponseEntity<Response<TutorialsDTO>> search(@PathVariable("search") String search) {
        
        Response<TutorialsDTO> response = new Response<>();
        if (search == null) {
            response.getErrors().add("Informe algo para pesquisar");
            return ResponseEntity.badRequest().body(response);
        }

        List<Tutorials> search2 = new ArrayList<>();

        if (isProd()) {
            search2 = tutorialsRepositoryPostgres.search(search);            
        } else {
            search2 = tutorialsRepositorySqlServer.search(search);
        }

        if (!search2.isEmpty()) {
            response.getErrors().add("Nenhum dado encontrado");
            return ResponseEntity.status(404).body(response);
        }
        
        search2.forEach(s -> {
            response.setData(tutorialsConverter.entityToDTO(s));
        });
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<TutorialsDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<TutorialsDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Tutorials> doc = tutorialsRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        try {tutorialsRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Beneficio não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(tutorialsConverter.entityToDTO(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<TutorialsDTO>>> getAll() {
        
        Response<List<TutorialsDTO>> response = new Response<>();

        List<Tutorials> doc = tutorialsRepository.findAll();
        if (doc.isEmpty()) {
            response.getErrors().add("Beneficios não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<TutorialsDTO> eventos = new ArrayList<>();
        doc.forEach(u -> {
            eventos.add(tutorialsConverter.entityToDTO(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }


    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
