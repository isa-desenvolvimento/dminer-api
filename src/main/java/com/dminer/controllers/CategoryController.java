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
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryConverter categoryConverter;


    private void validateRequestDto(CategoryRequestDTO dto, BindingResult result) {
        if (dto.getName() == null || dto.getName().isEmpty())  {
            result.addError(new ObjectError("dto", "Categoria precisa estar preenchido."));			
		}
    }

    private void validateDto(CategoryDTO dto, BindingResult result) {
        if (dto.getId() == null)  {
            result.addError(new ObjectError("dto", "Id precisa estar preenchido."));			
		}

        if (dto.getName() == null || dto.getName().isEmpty())  {
            result.addError(new ObjectError("dto", "Categoria precisa estar preenchido."));			
		}
    }
    
    @PostMapping()
    public ResponseEntity<Response<CategoryDTO>> create(@Valid @RequestBody CategoryRequestDTO dto, BindingResult result) {        

		log.info("Salvando uma nova categoria {}", dto.getName());

        Response<CategoryDTO> response = new Response<>();

        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

       Category category = categoryRepository.save(categoryConverter.requestDtoToEntity(dto));
        
        response.setData(categoryConverter.entityToDTO(category));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<CategoryDTO>> put( @Valid @RequestBody CategoryDTO dto, BindingResult result) {

        log.info("Alterando um categoria {}", dto);

        Response<CategoryDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando CategoryRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Category> optProfile = categoryRepository.findById(dto.getId());
        if (! optProfile.isPresent()) {
            log.info("Categoria não encontrado: {}", dto);
            response.getErrors().add("Categoria não encontrado");
            return ResponseEntity.ok().body(response);
        }

        optProfile.get().setName(dto.getName());

       Category category = categoryRepository.save(optProfile.get());
        response.setData(categoryConverter.entityToDTO(category));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<Response<CategoryDTO>> get(@PathVariable("id") Integer id) {
        log.info("Buscando categoria {}", id);
        
        Response<CategoryDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.ok().body(response);
        }

        Optional<Category> category = categoryRepository.findById(id);
        if (!category.isPresent()) {
            response.getErrors().add("Categoria não encontrado");
            return ResponseEntity.ok().body(response);
        }

        response.setData(categoryConverter.entityToDTO(category.get()));
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/dropdown")
    public ResponseEntity<Response<List<CategoryDTO>>> dropdown() {
        
        Response<List<CategoryDTO>> response = new Response<>();

        List<Category> category = categoryRepository.findAll();
        if (category == null || category.isEmpty()) {
            response.getErrors().add("Categorias não encontradas");
            return ResponseEntity.ok().body(response);
        }

        List<CategoryDTO> ps = new ArrayList<>();
        category.forEach(p -> {
            ps.add(categoryConverter.entityToDTO(p));
        });
        response.setData(ps);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<Boolean>> delete(@PathVariable("id") Integer id) {
        log.info("Deletando categoria {}", id);
        
        Response<Boolean> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        try {categoryRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.getErrors().add("Categoria não encontrada");
            return ResponseEntity.ok().body(response);
        }

        response.setData(true);
        return ResponseEntity.ok().body(response);
    }


}
