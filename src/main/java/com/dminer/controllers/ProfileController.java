package com.dminer.controllers;

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

import com.dminer.converters.ProfileConverter;
import com.dminer.dto.ProfileDTO;
import com.dminer.dto.ProfileRequestDTO;
import com.dminer.entities.Profile;
import com.dminer.repository.ProfileRepository;
import com.dminer.response.Response;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private static final Logger log = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileConverter profileConverter;


    private void validateRequestDto(ProfileRequestDTO dto, BindingResult result) {
        if (dto.getDescrible() == null || dto.getDescrible().isEmpty())  {
            result.addError(new ObjectError("dto", "Perfil precisa estar preenchido."));			
		}
    }

    private void validateDto(ProfileDTO dto, BindingResult result) {
        if (dto.getId() == null)  {
            result.addError(new ObjectError("dto", "Id precisa estar preenchido."));			
		}

        if (dto.getDescrible() == null || dto.getDescrible().isEmpty())  {
            result.addError(new ObjectError("dto", "Perfil precisa estar preenchido."));			
		}
    }
    
    @PostMapping()
    public ResponseEntity<Response<ProfileDTO>> create(@Valid @RequestBody ProfileRequestDTO dto, BindingResult result) {        

		log.info("Salvando um novo perfil {}", dto.getDescrible());

        Response<ProfileDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Profile profile = profileRepository.save(profileConverter.requestDtoToEntity(dto));
        
        response.setData(profileConverter.entityToDTO(profile));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<ProfileDTO>> put( @Valid @RequestBody ProfileDTO dto, BindingResult result) {

        log.info("Alterando um perfil {}", dto);

        Response<ProfileDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando ProfileRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Profile> optProfile = profileRepository.findById(dto.getId());
        if (! optProfile.isPresent()) {
            log.info("Perfil não encontrado: {}", dto);
            response.getErrors().add("Perfil não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        optProfile.get().setDescrible(dto.getDescrible());

        Profile profile = profileRepository.save(optProfile.get());
        response.setData(profileConverter.entityToDTO(profile));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<ProfileDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando perfil {}", id);
        
        Response<ProfileDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Profile> profile = profileRepository.findById(id);
        if (!profile.isPresent()) {
            response.getErrors().add("Perfil não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(profileConverter.entityToDTO(profile.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<Response<List<ProfileDTO>>> getAll() {
        
        Response<List<ProfileDTO>> response = new Response<>();

        List<Profile> profile = profileRepository.findAll();
        if (profile == null || profile.isEmpty()) {
            response.getErrors().add("Perfis não encontrados");
            return ResponseEntity.badRequest().body(response);
        }

        profile.forEach(p -> {
            response.getData().add(profileConverter.entityToDTO(p));
        });
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        log.info("Deletando perfil {}", id);
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {profileRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Perfil não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


}
