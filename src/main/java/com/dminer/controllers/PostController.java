package com.dminer.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.constantes.Constantes;
import com.dminer.converters.CommentConverter;
import com.dminer.dto.PostDTO;
import com.dminer.dto.PostRequestDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.FileInfo;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.enums.PostType;
import com.dminer.response.Response;
import com.dminer.services.CommentService;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilNumbers;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/post")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PostController {

	private static final Logger log = LoggerFactory.getLogger(PostController.class);
	private final String ROOT_UPLOADS = Constantes.ROOT_UPLOADS;
	
	@Autowired
	private FileStorageService fileStorageService;
	
	@Autowired
	private FileDatabaseService fileDatabaseService;
	
	@Autowired
	private PostService postService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CommentConverter commentConverter;
    
	private Gson gson = new Gson();

	
	@PostMapping(consumes = {"multipart/form-data", "text/plain"})
	public ResponseEntity<Response<PostDTO>> create( @RequestParam(value = "files", required = false) MultipartFile[] files,  @RequestParam("postRequestDTO") String data ) {
		
		log.info("----------------------------------------");
		log.info("Salvando um novo post {}", data);

		Response<PostDTO> response = new Response<>();
		
		PostDTO postRequestDTO = gson.fromJson(data, PostDTO.class);
		
		log.info("Verificando se o usuário informado existe");
		if (postRequestDTO.getUser().getLogin() == null ) {
			response.getErrors().add("Usuário não encontrado.");			
		}
		
		try {
			PostType.valueOf(postRequestDTO.getType());				
		} catch (IllegalArgumentException e) {
			response.getErrors().add("Campo tipo é inválido.");
		}
		
		if (!response.getErrors().isEmpty()) {
			return ResponseEntity.badRequest().body(response);
		}
		
		Post post = new Post();
		if (postRequestDTO.getContent() != null) 
			post.setContent(postRequestDTO.getContent());
		
		if (UtilNumbers.isNumeric(postRequestDTO.getLikes() + ""))
			post.setLikes(postRequestDTO.getLikes());

		if (postRequestDTO.getType() != null && !postRequestDTO.getType().isEmpty()) {
			post.setType(PostType.valueOf(postRequestDTO.getType()));
		}

		post.setLogin(postRequestDTO.getUser().getLogin());
		post = postService.persist(post);


		
		String caminhoAbsoluto;
		try {
			caminhoAbsoluto = criarDiretorio(post.getId());
		} catch(IOException e) {
			rollback(post, null, null);
			response.getErrors().add(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}

		List<FileInfo> anexos = new ArrayList<>();

		Path path = Paths.get(caminhoAbsoluto);
		if (files != null && files.length > 0) {
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
		}
		
		Post temp = postService.persist(post);
		log.info("Adicionando anexos ao post e atualizando. {}", temp);
		
		response.setData(postToDto(temp, anexos, null));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
    
	
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") int id) {
		
		Response<String> response = new Response<>();

		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			log.info("Deletando Post {}", post.get());

			Optional<List<Comment>> comments = commentService.findByPost(post.get());
			if (comments.isPresent() && !comments.get().isEmpty()) {
				comments.get().forEach(comment -> {
			 		commentService.delete(comment.getId());
			 	});
			}
			
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
	public ResponseEntity<Response<PostDTO>> get(@PathVariable("id") int id) {
		
		Response<PostDTO> response = new Response<>();
		log.info("Recuperando Post {}", id);

		Optional<Post> post = postService.findById(id);
		if (!post.isPresent()) {
			response.getErrors().add("Post não encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		Optional<List<FileInfo>> anexos = fileDatabaseService.findByPost(post.get());
		
		Optional<List<Comment>> comment = commentService.findByPost(post.get());
		response.setData(postToDto(post.get(), anexos.get(), comment.get()));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	private void rollback(Post post, List<FileInfo> anexos, List<Comment> comments) {
				
		if (anexos != null) {
			log.info("Apagando os anexos do banco");		
			anexos.forEach(anexo -> {
				fileDatabaseService.delete(anexo.getId());				
			});
		}

		if (comments != null) {
			log.info("Apagando os comentários do banco");
			comments.forEach(comment -> {
				commentService.delete(comment.getId());
			});
		}

		if (post != null) {
			log.info("Apagando o post do banco");
			postService.delete(post.getId());
			fileStorageService.delete(Paths.get(Constantes.appendInRoot(post.getId() + "")));
		}
	}

	private String token = null;
	private PostDTO postToDto(Post post, List<FileInfo> anexos, List<Comment> comments) {
		PostDTO dto = new PostDTO();
		System.out.println(post.toString());
		
		dto.setLikes(post.getLikes());
		dto.setType(post.getType().toString());
		dto.setId(post.getId());		
		dto.setContent(post.getContent());
		dto.setTitle(post.getTitle());
		anexos.forEach(e -> {
			dto.getAnexos().add(e.getUrl());
		});

		if (token == null) {
            token = userService.getToken();
        }
        byte[] avatar = userService.getAvatar(post.getLogin());
        String encodedString = Base64.getEncoder().encodeToString(avatar);  
        
        dto.setUser(new UserReductDTO(post.getLogin(), null, encodedString));
        
		if (comments != null && !comments.isEmpty()) {
			comments.forEach(e -> {
				dto.getComments().add(commentConverter.entityToDTO(e));
			});			
		}
		 
		return dto;
	}


	
	
	@GetMapping
	public ResponseEntity<Response<List<PostDTO>>> getAll() {
		
		Response<List<PostDTO>> response = new Response<>();
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAll();
		if (posts == null || posts.isEmpty()) {
			response.getErrors().add("Nenhum post encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		for (Post post2 : posts) {
			Optional<List<FileInfo>> anexos = fileDatabaseService.findByPost(post2);
			Optional<List<Comment>> comment = commentService.findByPost(post2);
			PostDTO dto = postToDto(post2, anexos.get(), comment.get());
			response.getData().add(dto);			
		}		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	
	/**
	 * Cria diretórios organizados pelo id do Post
	 */
	private String criarDiretorio(int idPost) throws IOException {
		String diretorio = Constantes.appendInRoot(idPost + "");
		log.info("Criando diretório {}", diretorio);
		fileStorageService.createDirectory(Paths.get(diretorio));
		return diretorio;
	}

	

}
