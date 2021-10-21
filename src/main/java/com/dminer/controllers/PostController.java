package com.dminer.controllers;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.constantes.Constantes;
import com.dminer.dto.PostDTO;
import com.dminer.entities.FileInfo;
import com.dminer.entities.Post;
import com.dminer.response.Response;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.PostService;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
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
		log.info("----------------------------------------");
		log.info("Salvando um novo post {}", contentPost);
    	Response<PostDTO> response = new Response<>();
    	Post post = postService.persist(new Post());

		if (contentPost != null) post.setContent(contentPost);

		String caminhoAbsoluto;
		try {
			caminhoAbsoluto = criarDiretorio(post.getId());
		} catch(RuntimeException e) {
			postService.delete(post.getId());
			response.getErrors().add(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

		List<FileInfo> anexos = new ArrayList<>();

    	try {
			Path path = Paths.get(caminhoAbsoluto);
			List<MultipartFile> array = Arrays.asList(files);

			Optional<FileInfo> info = Optional.empty();

			for (MultipartFile multipartFile : array) {				
				String arquivoUrl = caminhoAbsoluto + "\\" + multipartFile.getOriginalFilename();

				if (fileStorageService.existsDirectory(Paths.get(arquivoUrl))) {
					log.info("Diretório já existe no storage = {}", arquivoUrl);
					info = fileDatabaseService.persist(new FileInfo(arquivoUrl, post));

				} else {
					if(fileStorageService.save(multipartFile, path)) {
						log.info("Salvando novo arquivo no storage {}", arquivoUrl);
						info = fileDatabaseService.persist(new FileInfo(arquivoUrl, post));
					}
				}
				
				if (info.isPresent()) {					
					anexos.add(info.get());
				}
			}
    	    
			Post temp = postService.persist(post);
			log.info("Adicionando anexos ao post e atualizando. {}", temp);
			
			response.setData(toDto(temp, anexos));
			return ResponseEntity.status(HttpStatus.OK).body(response);

		} catch (Exception e) {
			rollback(post, anexos);
			response.getErrors().add("Erro ao salvar post. Erro: " + e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    	}
	}
    
	
	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<Response<String>> deletePost(@PathVariable("id") int id) {
		
		Response<String> response = new Response<>();

		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			log.info("Deletando Post {}", post.get());

			Optional<List<FileInfo>> findByPost = fileDatabaseService.findByPost(post.get());
			if (findByPost.isPresent()) {
				findByPost.get().forEach(anexo -> {
					fileDatabaseService.delete(anexo.getId());
					log.info("Deletando anexo {}", anexo.getUrl());
				});
			}
			postService.delete(post.get().getId());
			response.setData("Post deletado!");

			fileStorageService.delete(Paths.get(Constantes.appendInRoot(post.get().getId() + "")));
			if (fileStorageService.existsDirectory(Paths.get(ROOT_UPLOADS))) {		
				response.getErrors().add("Diretório do Post não foi deletado corretamente");
				return ResponseEntity.internalServerError().body(response);
			}			
		} else {
			response.getErrors().add("Post não encontrado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		return ResponseEntity.ok().body(response);
	}


	@PostMapping(value = "/drop-storage")
	public boolean dropStorage() {
		fileStorageService.delete(Paths.get(ROOT_UPLOADS));
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


	private void rollback(Post post, List<FileInfo> anexos) {
		anexos.forEach(anexo -> {
			fileDatabaseService.delete(anexo.getId());
		});
		postService.delete(post.getId());
		fileStorageService.delete(Paths.get(Constantes.appendInRoot(post.getId() + "")));
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
