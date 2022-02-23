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

import com.dminer.converters.PriorityConverter;
import com.dminer.dto.PriorityDTO;
import com.dminer.dto.PriorityRequestDTO;
import com.dminer.entities.Priority;
import com.dminer.repository.PriorityRepository;
import com.dminer.response.Response;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/priority")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PriorityController {

    private static final Logger log = LoggerFactory.getLogger(PriorityController.class);

    @Autowired
    private PriorityRepository priorityRepository;

    @Autowired
    private PriorityConverter priorityConverter;


    private void validateRequestDto(PriorityRequestDTO dto, BindingResult result) {
        if (dto.getName() == null || dto.getName().isEmpty())  {
            result.addError(new ObjectError("dto", "Prioridade precisa estar preenchido."));			
		}
    }

    private void validateDto(PriorityDTO dto, BindingResult result) {
        if (dto.getId() == null)  {
            result.addError(new ObjectError("dto", "Id precisa estar preenchido."));			
		}

        if (dto.getName() == null || dto.getName().isEmpty())  {
            result.addError(new ObjectError("dto", "Prioridade precisa estar preenchido."));			
		}
    }
    
    @PostMapping()
    public ResponseEntity<Response<PriorityDTO>> create(@Valid @RequestBody PriorityRequestDTO dto, BindingResult result) {        

		log.info("Salvando uma nova prioridade {}", dto.getName());

        Response<PriorityDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

       Priority priority = priorityRepository.save(priorityConverter.requestDtoToEntity(dto));
        
        response.setData(priorityConverter.entityToDTO(priority));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<PriorityDTO>> put( @Valid @RequestBody PriorityDTO dto, BindingResult result) {

        log.info("Alterando um prioridade {}", dto);

        Response<PriorityDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando PriorityRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Priority> optProfile = priorityRepository.findById(dto.getId());
        if (! optProfile.isPresent()) {
            log.info("Prioridade não encontrado: {}", dto);
            response.getErrors().add("Prioridade não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        optProfile.get().setName(dto.getName());

       Priority priority = priorityRepository.save(optProfile.get());
        response.setData(priorityConverter.entityToDTO(priority));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<PriorityDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando prioridade {}", id);
        
        Response<PriorityDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Priority> priority = priorityRepository.findById(id);
        if (!priority.isPresent()) {
            response.getErrors().add("Prioridade não encontrado");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(priorityConverter.entityToDTO(priority.get()));
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/dropdown")
    public ResponseEntity<Response<List<PriorityDTO>>> dropdown() {
        
        Response<List<PriorityDTO>> response = new Response<>();

        List<Priority> priority = priorityRepository.findAll();
        if (priority == null || priority.isEmpty()) {
            response.getErrors().add("Prioridades não encontradas");
            return ResponseEntity.badRequest().body(response);
        }

        List<PriorityDTO> ps = new ArrayList<>();
        priority.forEach(p -> {
            ps.add(priorityConverter.entityToDTO(p));
        });
        response.setData(ps);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        log.info("Deletando prioridade {}", id);
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {priorityRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Prioridade não encontrada");
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


}
