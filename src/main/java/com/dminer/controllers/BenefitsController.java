package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.dminer.constantes.MessagesConst;
import com.dminer.converters.BenefitsConverter;
import com.dminer.dto.BenefitsRequestDTO;
import com.dminer.dto.BenefitsDTO;
import com.dminer.entities.Benefits;
import com.dminer.repository.BenefitsRepository;
import com.dminer.response.Response;
import com.dminer.services.BenefitsService;
import com.dminer.utils.UtilDataHora;
import com.dminer.validadores.Validators;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/benefits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class BenefitsController {
    
    private static final Logger log = LoggerFactory.getLogger(BenefitsController.class);

    @Autowired 
    private Validators validators;

    @Autowired 
    private BenefitsConverter benefitsConverter;

    @Autowired
    private BenefitsRepository benefitsRepository;

    @Autowired
    private BenefitsService benefitsService;


    @PostMapping
    public ResponseEntity<Response<BenefitsDTO>> create(@Valid @RequestBody BenefitsRequestDTO dto, BindingResult result) {
    
		Response<BenefitsDTO> response = new Response<>();
        
        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }
        
        Benefits entity = benefitsRepository.save(benefitsConverter.dtoRequestToEntity(dto));
        response.setData(benefitsConverter.entityToDto(entity));

        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<BenefitsDTO>> put( @Valid @RequestBody BenefitsDTO dto, BindingResult result) {

        log.info(MessagesConst.ALTERANDO_REGISTRO, dto);

        Response<BenefitsDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }

        Benefits benefits = benefitsRepository.save(benefitsConverter.dtoToEntity(dto));
        response.setData(benefitsConverter.entityToDto(benefits));
        return ResponseEntity.ok().body(response);
    }
    
    
    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<BenefitsDTO>> get(@PathVariable("id") Integer id) {
        
        Response<BenefitsDTO> response = new Response<>();
        if (id == null) {
            response.addError(MessagesConst.INFORME_ID);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Benefits> entity = benefitsRepository.findById(id);
        if (!entity.isPresent()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        response.setData(benefitsConverter.entityToDto(entity.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<BenefitsDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<BenefitsDTO> response = new Response<>();
        if (id == null) {
            response.addError(MessagesConst.INFORME_ID);
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Benefits> enetity = benefitsRepository.findById(id);
        if (!enetity.isPresent()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        try {benefitsRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }

        response.setData(benefitsConverter.entityToDto(enetity.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<BenefitsDTO>>> getAll(@RequestHeader("x-access-adminUser") String perfil) {
        
        Response<List<BenefitsDTO>> response = new Response<>();

        List<Benefits> doc = benefitsService.getAllByPermission(perfil);
        if (doc.isEmpty()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }
        
        // ordenar do mais novo pro mais antigo
		doc = doc.stream()
		.sorted(Comparator.comparing(Benefits::getDate).reversed())
		.collect(Collectors.toList());
        
        List<BenefitsDTO> eventos = new ArrayList<>();
        doc.forEach(u -> {        	
            eventos.add(benefitsConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
    

    @GetMapping(value = "/search/{keyword}")
    public ResponseEntity<Response<List<BenefitsDTO>>> search(@RequestHeader("x-access-adminUser") String perfil, @PathVariable String keyword) {
        
        Response<List<BenefitsDTO>> response = new Response<>();
        // if (keyword == null || keyword.isBlank()) {
        //     response.addError(MessagesConst.INFORME_TERMO);
        //     return ResponseEntity.badRequest().body(response);
        // }

        if (keyword.equalsIgnoreCase("null")) keyword = null;
        
        List<Benefits> entities = benefitsService.search(keyword);

        if (entities == null || entities.isEmpty()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.ok().body(response);
        }
        
        entities = entities.stream()
		.sorted(Comparator.comparing(Benefits::getDate).reversed())
		.collect(Collectors.toList());

        if (perfil.equalsIgnoreCase("0")) {
            entities = entities.stream().filter(e -> e.getPermission().getId() == 0).collect(Collectors.toList());
        }


        List<BenefitsDTO> ret = new ArrayList<>();
        for (Benefits entity : entities) {        	
        	ret.add(benefitsConverter.entityToDto(entity));
		}
        
        response.setData(ret);
        return ResponseEntity.ok().body(response);
    }
    

    private void validateRequestDto(BenefitsRequestDTO dto, BindingResult result) {
        String login = dto.getCreator();
        if (! validators.existsUserByLogin(login)) {
            result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
        }

        if (!UtilDataHora.isTimestampValid(dto.getDate())) {
            result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss, porém foi informado: " + dto.getDate()));
        }
    }
    
   
    private void validateDto(BenefitsDTO dto, BindingResult result) {

        if(! validators.existsBenefitsById(dto.getId())) {
            result.addError(new ObjectError("dto", "Id do Benefits não é válida."));
        }

        if(!validators.existsPermissionById(dto.getPermission())) {
            result.addError(new ObjectError("dto", "Permissão não é válida."));
        }
        
        String login = dto.getCreator();
        if (! validators.existsUserByLogin(login)) {
            result.addError(new ObjectError("dto", "Usuário: " + login + " não encontrado."));
        }

        if (!UtilDataHora.isTimestampValid(dto.getDate())) {
            result.addError(new ObjectError("dto", "Data precisa estar preenchida no formato yyyy-mm-dd hh:mm:ss, porém foi informado: " + dto.getDate()));
        }
    }

    
}
