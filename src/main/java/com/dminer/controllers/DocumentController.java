package com.dminer.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import com.dminer.converters.DocumentConverter;
import com.dminer.dto.DocumentRequestDTO;
import com.dminer.dto.DocumentDTO;
import com.dminer.dto.DocumentLoadDTO;
import com.dminer.entities.Document;
import com.dminer.repository.CategoryRepository;
import com.dminer.repository.DocumentRepository;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.response.Response;
import com.dminer.utils.UtilFilesStorage;

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
import org.springframework.web.bind.annotation.RequestHeader;
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

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private GenericRepositorySqlServer genericRepositorySqlServer;
    
    @Autowired
    private Environment env;


    @PostMapping
    public ResponseEntity<Response<DocumentDTO>> create(@Valid @RequestBody DocumentRequestDTO dto, BindingResult result) {
    
		Response<DocumentDTO> response = new Response<>();
        validateRequestDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando dto: {}", dto);
            result.getAllErrors().forEach( e -> response.addError(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
        
        Document doc = documentConverter.requestDtoToEntity(dto);
        
        if (!dto.getContentLink().isBlank()) {
            
            // String systemOp = System.getProperty("os.name");
            // if (systemOp.equalsIgnoreCase("linux")) {
            // }
            
            if (dto.getContentLink().contains("\\")) {
                dto.setContentLink(dto.getContentLink().replace("\\", "/"));
            }

            String nomeArquivo = UtilFilesStorage.getNomeArquivo(dto.getContentLink(), "/");
            String link = env.getProperty("diretorio-uploads") + nomeArquivo;

            log.info("Tentando copiar arquivo: {}", link);

            boolean copiou = UtilFilesStorage.copyFiles4(dto.getContentLink(), link);
            if (!copiou) {
                response.addError("Erro ao copiar arquivo: " + dto.getContentLink());
            } else {
                log.info("Arquivo copiado com sucesso para: {}", link);
                dto.setContentLink(link);                
            }
        }
        doc = documentRepository.save(doc);
        DocumentDTO dtoTemp = documentConverter.entityToDto(doc);
        
        response.setData(dtoTemp);
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/load")
    public ResponseEntity<String> loadFileFromLocal(@RequestBody DocumentLoadDTO filePath) {
        String file = UtilFilesStorage.loadFile(filePath.getPath());
        System.out.println(file);
        return ResponseEntity.ok(file);
    }
    

    @GetMapping(value = "/find/{id}")
    public ResponseEntity<Response<DocumentDTO>> get(@PathVariable("id") Integer id) {
        
        Response<DocumentDTO> response = new Response<>();
        if (id == null) {
            response.addError("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Document> doc = documentRepository.findById(id);
        if (!doc.isPresent()) {
            response.addError("Documento não encontrado");
            return ResponseEntity.ok().body(response);
        }

        response.setData(documentConverter.entityToDto(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Response<DocumentDTO>> delete(@PathVariable("id") Integer id) {
        
        Response<DocumentDTO> response = new Response<>();
        if (id == null) {
            response.addError("Informe um id");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<Document> doc = documentRepository.findById(id);
        if (!doc.isPresent()) {
            response.addError("Documento não encontrado");
            return ResponseEntity.ok().body(response);
        }

        try {documentRepository.deleteById(id);}
        catch (EmptyResultDataAccessException e) {
            response.addError("Documento não encontrado");
            return ResponseEntity.ok().body(response);
        }

        response.setData(documentConverter.entityToDto(doc.get()));
        return ResponseEntity.ok().body(response);
    }


    @PutMapping()
    public ResponseEntity<Response<DocumentDTO>> put( @Valid @RequestBody DocumentDTO dto, BindingResult result) {

        log.info("Alterando um categoria {}", dto);

        Response<DocumentDTO> response = new Response<>();

        validateDto(dto, result);
        if (result.hasErrors()) {
            log.info("Erro validando CategoryRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.addError(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }

        Document doc2 = documentRepository.save(documentConverter.dtoToEntity(dto));
        response.setData(documentConverter.entityToDto(doc2));
        return ResponseEntity.ok().body(response);
    }


    @GetMapping("/all")
    public ResponseEntity<Response<List<DocumentDTO>>> getAll() {
        
        Response<List<DocumentDTO>> response = new Response<>();

        List<Document> doc = documentRepository.findAllByOrderByCreateDateDesc();
        if (doc.isEmpty()) {
            response.addError("Documentos não encontrados");
            return ResponseEntity.ok().body(response);
        }

        List<DocumentDTO> eventos = new ArrayList<>();
        doc.forEach(u -> {
            eventos.add(documentConverter.entityToDto(u));
        });        
        response.setData(eventos);
        return ResponseEntity.ok().body(response);
    }
    
    
    @GetMapping(value = "/search/{keyword}")
    public ResponseEntity<Response<List<DocumentDTO>>> search(@PathVariable String keyword) {
        
        Response<List<DocumentDTO>> response = new Response<>();
        if (keyword == null || keyword.isBlank()) {
            response.addError("Informe um termo");
            return ResponseEntity.badRequest().body(response);
        }

        List<Document> doc = genericRepositorySqlServer.searchDocuments(keyword);
        if (doc == null || doc.isEmpty()) {
            return ResponseEntity.ok().body(response);
        }
        
        List<DocumentDTO> ret = new ArrayList<>();
        for (Document document : doc) {        	
        	ret.add(documentConverter.entityToDto(document));
		}
        
        response.setData(ret);
        return ResponseEntity.ok().body(response);
    }
    
    private void validateRequestDto(DocumentRequestDTO dto, BindingResult result) {
        if (dto.getCategory() == null) {
            result.addError(new ObjectError("dto", "Categoria precisa estar preenchido."));
		} else {
            if(!categoryRepository.existsById(dto.getCategory())) {
                result.addError(new ObjectError("dto", "Categoria não é válida."));
            }
        }

        if (dto.getPermission() == null) {
            dto.setPermission(false);
        }

        if (dto.getContentLink() == null) {
            result.addError(new ObjectError("dto", "Conteúdo precisa estar preenchido."));
		}

        if (dto.getTitle() == null) {
            result.addError(new ObjectError("dto", "Title precisa estar preenchido."));
		}
    }
    

    private void validateDto(DocumentDTO dto, BindingResult result) {
        if (dto.getId() == null) {
            result.addError(new ObjectError("dto", "Id do Documento precisa estar preenchido."));
		} else {
            if(!documentRepository.existsById(dto.getId())) {
                result.addError(new ObjectError("dto", "Documento não encontrado."));
            }
        }

        if (dto.getCategory() == null) {
            result.addError(new ObjectError("dto", "Categoria precisa estar preenchido."));
		} else {
            if(!categoryRepository.existsById(dto.getCategory())) {
                result.addError(new ObjectError("dto", "Categoria não é válida."));
            }
        }

        if (dto.getPermission() == null) {
            dto.setPermission(false);
        }

        if (dto.getTitle() == null || dto.getTitle().isEmpty())  {
            result.addError(new ObjectError("dto", "Categoria precisa estar preenchido."));			
		}
    }
}
