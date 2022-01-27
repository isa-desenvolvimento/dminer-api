package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.constantes.MessagesConst;
import com.dminer.converters.CategoryConverter;
import com.dminer.dto.CategoryDTO;
import com.dminer.dto.CategoryRequestDTO;
import com.dminer.entities.Category;
import com.dminer.repository.CategoryRepository;
import com.dminer.response.Response;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryConverter categoryConverter;

    
    @PostMapping()
    public ResponseEntity<Response<CategoryDTO>> create(@Valid @RequestBody CategoryRequestDTO dto, BindingResult result) {
		log.info(MessagesConst.SALVANDO_REGISTRO, dto.getTitle());

        Response<CategoryDTO> response = new Response<>();

        Category category = categoryRepository.save(categoryConverter.dtoRequestToEntity(dto));
        
        response.setData(categoryConverter.entityToDto(category));
        return ResponseEntity.status(201).body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<CategoryDTO>> put( @Valid @RequestBody CategoryDTO dto, BindingResult result) {

        log.info(MessagesConst.ALTERANDO_REGISTRO, dto);
        Response<CategoryDTO> response = new Response<>();

        Category category = categoryRepository.save(
            categoryConverter.dtoToEntity(dto)
        );

        response.setData(
            categoryConverter.entityToDto(category)
        );
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<CategoryDTO>> get(@PathVariable("id") @NotNull(message = MessagesConst.INFORME_ID) Integer id) {
        log.info("Buscando categoria {}", id);
        
        Response<CategoryDTO> response = new Response<>();

        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.badRequest().body(response);
        }
        response.setData(categoryConverter.entityToDto(category.get()));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/all")
    public ResponseEntity<Response<List<CategoryDTO>>> getAll() {
        
        Response<List<CategoryDTO>> response = new Response<>();

        List<Category> category = categoryRepository.findAll();
        if (category == null || category.isEmpty()) {
            response.addError(MessagesConst.NENHUM_REGISTRO_ENCONTRADO);
            return ResponseEntity.badRequest().body(response);
        }

        List<CategoryDTO> dto = new ArrayList<>();
        category.forEach(categoryTemp -> {
            dto.add(categoryConverter.entityToDto(categoryTemp));
        });
        response.setData(dto);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") @NotNull(message = MessagesConst.INFORME_ID) Integer id) {
        log.info(MessagesConst.EXCLUINDO_REGISTRO, id);
        
        Response<Boolean> response = new Response<>();
       
        try {categoryRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.addError(MessagesConst.EXCLUINDO_REGISTRO);
            return ResponseEntity.badRequest().body(response);
        }

        response.setData(true);
        return ResponseEntity.accepted().body(response);
    }


}
