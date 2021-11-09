package com.dminer.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.DocumentConverter;
import com.dminer.converters.NoticeConverter;
import com.dminer.dto.DocumentRequestDTO;
import com.dminer.dto.DocumentDTO;
import com.dminer.dto.NoticeRequestDTO;
import com.dminer.entities.Document;
import com.dminer.entities.Notice;
import com.dminer.entities.User;
import com.dminer.enums.Category;
import com.dminer.enums.Permissions;
import com.dminer.repository.DocumentRepository;
import com.dminer.response.Response;
import com.dminer.services.NoticeService;
import com.dminer.services.UserService;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/document")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DocumentController {
    
    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    @Autowired 
    private DocumentConverter documentConverter;

    @Autowired
    private DocumentRepository documentRepository;

    private Category cat = null;    
    private Permissions per = null;

    private void validateRequestDto(DocumentRequestDTO dto, BindingResult result) {
        if (dto.getCategory() == null) {
            result.addError(new ObjectError("DocumentRequestDTO", "Categoria precisa estar preenchido."));
		} else {
            Arrays.asList(Category.values()).forEach(c -> {
                if (c.name().equals(dto.getCategory())) {
                    cat = Category.valueOf(dto.getCategory());
                    return;
                }
            });

            if (cat == null) {
                result.addError(new ObjectError("DocumentRequestDTO", "Categoria não é válida."));
            }
        }

        if (dto.getPermission() == null) {
            result.addError(new ObjectError("DocumentRequestDTO", "Permissão precisa estar preenchido."));
		} else {
            Arrays.asList(Permissions.values()).forEach(c -> {
                if (c.name().equals(dto.getPermission())) {
                    per = Permissions.valueOf(dto.getPermission());
                }
            });

            if (per == null) {
                result.addError(new ObjectError("DocumentRequestDTO", "Permissão não é válida."));
            }
        }

        if (dto.getContentLink() == null) {
            result.addError(new ObjectError("DocumentRequestDTO", "Conteúdo precisa estar preenchido."));
		}

        if (dto.getTitle() == null) {
            result.addError(new ObjectError("DocumentRequestDTO", "Title precisa estar preenchido."));
		}
    }
    

    @PostMapping
    public ResponseEntity<Response<DocumentDTO>> create(@Valid @RequestBody DocumentRequestDTO dto, BindingResult result) {
    
		Response<DocumentDTO> response = new Response<>();
        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Document doc = documentRepository.save(documentConverter.requestDtoToEntity(dto));
        response.setData(documentConverter.entityToDto(doc));

        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<DocumentDTO>> get(@PathVariable("id") Integer id) {
        
        Response<DocumentDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Document> doc = documentRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Documento não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(documentConverter.entityToDto(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<DocumentDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<DocumentDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Document> doc = documentRepository.findById(id);
        if (!doc.isPresent()) {
            response.getErrors().add("Documento não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        try {documentRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Documento não encontrado");
            return ResponseEntity.status(404).body(response);
        }

        response.setData(documentConverter.entityToDto(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<DocumentDTO>>> getAll() {
        
        Response<List<DocumentDTO>> response = new Response<>();

        List<Document> doc = documentRepository.findAll();
        if (doc.isEmpty()) {
            response.getErrors().add("Documentos não encontrados");
            return ResponseEntity.status(404).body(response);
        }

        List<DocumentDTO> eventos = new ArrayList<>();
        doc.forEach(u -> {
            eventos.add(documentConverter.entityToDto(u));
        });
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
}
