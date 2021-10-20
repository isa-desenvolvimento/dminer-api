package com.dminer.dminer.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.dminer.constantes.Constantes;
import com.dminer.dminer.dto.PostDTO;
import com.dminer.dminer.entities.FileInfo;
import com.dminer.dminer.entities.Post;
import com.dminer.dminer.response.Response;
import com.dminer.dminer.services.FileDatabaseService;
import com.dminer.dminer.services.FileStorageService;
import com.dminer.dminer.services.PostService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/post")
@CrossOrigin
@RequiredArgsConstructor
public class PostController {

	private static final Logger log = LoggerFactory.getLogger(PostController.class);
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private FileDatabaseService fileDatabaseService;
	
	@Autowired
	private PostService postService;
	
	
	private final String ROOT_UPLOADS = Constantes.ROOT_UPLOADS;


	
    @PostMapping
	public ResponseEntity<Response<PostDTO>> create(@RequestParam MultipartFile[] files, @RequestParam String contentPost) {
		log.error("----------------------------------------");
		log.info("Salvando um novo post {}", contentPost);
    	Response<PostDTO> response = new Response<>();
    	Post post = postService.persist(new Post());

		String caminhoAbsoluto;
		try {
			caminhoAbsoluto = criarDiretorio(post.getId());
		} catch(RuntimeException e) {
			response.getErrors().add(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

		List<FileInfo> anexos = new ArrayList<>();

    	try {
			Path path = Paths.get(caminhoAbsoluto);
    	    Arrays.asList(files).stream().forEach(file -> {
				
				String arquivoUrl = caminhoAbsoluto + "\\" + file.getOriginalFilename();

				// se o storage já tem arquivos tenta atualizar o banco
				Optional<FileInfo> info = Optional.empty();
				if (fileStorageService.existsDirectory(path)) {
					info = fileDatabaseService.persist(new FileInfo(arquivoUrl, post));
				} else {
					if(fileStorageService.save(file, path)) {
						log.info("Salvando arquivo {}", arquivoUrl);
						info = fileDatabaseService.persist(new FileInfo(arquivoUrl, post));
					}
				}

				if (info.isPresent()) {
					log.info("info -> {}", info.get());
					anexos.add(info.get());
				}
				
    	    });
    	    			
			Post temp = postService.persist(post);
			
			response.setData(toDto(temp, anexos));
			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {			
			response.getErrors().add("Erro ao salvar post. Erro: " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    	}
	}
    
	@PostMapping(value = "/drop-storage")
	public boolean dropStorage() {
		fileStorageService.deleteAll(Paths.get(ROOT_UPLOADS));
		return !fileStorageService.existsDirectory(Paths.get(ROOT_UPLOADS));
	}

	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<PostDTO>> getPost(@PathVariable("id") int id) {
		
		Response<PostDTO> response = new Response<>();
		log.info("Recuperando Post {}", id);

		Optional<Post> post = postService.findById(id);
		if (!post.isPresent()) {
			response.getErrors().add("Post não encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		Optional<List<FileInfo>> anexos = fileDatabaseService.findByPost(post.get());
		
		response.setData(toDto(post.get(), anexos.get()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	private PostDTO toDto(Post post, List<FileInfo> anexos) {
		PostDTO dto = new PostDTO();
		dto.setId(post.getId());
		dto.setContent(post.getContent());
		anexos.forEach(e -> {
			dto.getAnexos().add(e.getUrl());
		});		
		return dto;
	}
	/**
	 * Cria diretórios organizados pelo id do Post
	 */
	private String criarDiretorio(int idPost) throws RuntimeException {
		String diretorio = Constantes.appendInRoot(idPost + "");
		log.info("Verificando se o diretório existe {}", diretorio);
		fileStorageService.createDirectory(Paths.get(diretorio));
		return diretorio;
	}
}
