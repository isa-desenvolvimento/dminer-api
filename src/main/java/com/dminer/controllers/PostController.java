package com.dminer.controllers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.ws.rs.HeaderParam;

import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.components.TokenService;
import com.dminer.constantes.Constantes;
import com.dminer.converters.CommentConverter;
import com.dminer.dto.CommentDTO;
import com.dminer.dto.LikesDTO;
import com.dminer.dto.PostDTO;
import com.dminer.dto.PostRequestDTO;
import com.dminer.dto.ReactDTO;
import com.dminer.dto.SurveyRequestDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Favorites;
import com.dminer.entities.FileInfo;
import com.dminer.entities.ReactUser;
import com.dminer.entities.Post;
import com.dminer.entities.React;
import com.dminer.entities.User;
import com.dminer.enums.PostType;
import com.dminer.repository.CommentRepository;
import com.dminer.repository.FavoritesRepository;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.ReactRepository;
import com.dminer.repository.ReactUserRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.services.CommentService;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;
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
	private CommentRepository commentRepository;

	@Autowired
	private ReactRepository reactRepository;

	@Autowired
	private ReactUserRepository reactUserRepository;
	
	@Autowired
	private CommentConverter commentConverter;    
	
	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;
	
	@Autowired
	private FavoritesRepository favoritesRepository;



	private Gson gson = new Gson();
	
	private String token;

	//private UserRestModel userRestModel;


	private void validateRequestDto(PostRequestDTO dto, BindingResult result) {        
        if (dto.getLogin() == null || dto.getLogin().isBlank()) {
            result.addError(new ObjectError("dto", "Login precisa estar preenchido."));
        } 
        
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            result.addError(new ObjectError("dto", "Titulo precisa estar preenchido."));
        } 

        if (dto.getContent() == null || dto.getContent().isBlank()) {
            result.addError(new ObjectError("dto", "Conteúdo precisa estar preenchido."));
        }
        
        if (dto.getType() == null || dto.getType() < 1 || dto.getType() > 2) {
            result.addError(new ObjectError("dto", "Tipo informado precisa ser 1 para Interno ou 2 para Externo"));
        }
        
    }


	

	@PostMapping()
	public ResponseEntity<Response<PostDTO>> create(@RequestBody PostRequestDTO dto, BindingResult result) {
	
		Response<PostDTO> response = new Response<>();
		validateRequestDto(dto, result);
		if (result.hasErrors()) {
            log.info("Erro validando PostRequestDTO: {}", dto);
            result.getAllErrors().forEach( e -> response.getErrors().add(e.getDefaultMessage()));
            return ResponseEntity.badRequest().body(response);
        }
		
		Post post = new Post();
		if (dto.getAnexo() != null) {
			post.setAnexo(dto.getAnexo());
		}
		post.setContent(dto.getContent());		
		post.setLogin(dto.getLogin());
		post.setTitle(dto.getTitle());
		if (dto.getType() == 1) {
			post.setType(PostType.INTERNAL);
		} else {
			post.setType(PostType.EXTERNAL);
		}

		response.setData(postToDto(post, null));
		
		post = postService.persist(post);

		if (post.getType().equals(PostType.EXTERNAL)) {
			// salvar na api externa
			HttpStatus code = postService.salvarApiExterna(post);
			if (code.value() != 201) {
				return ResponseEntity.internalServerError().body(null);
			}
		}		
		return ResponseEntity.ok().body(response);
	}
	
	
	/**
	 * Método para futura melhorias
	 * @param files
	 * @param data
	 * @return
	 */
	//@PostMapping(consumes = {"multipart/form-data", "text/plain"})
	@Deprecated
	public ResponseEntity<Response<PostDTO>> create_old( @RequestParam(value = "files", required = false) MultipartFile[] files,  @RequestParam("postRequestDTO") String data ) {
		
		log.info("----------------------------------------");
		log.info("Salvando um novo post {}", data);

		Response<PostDTO> response = new Response<>();
		
		PostDTO postRequestDTO = gson.fromJson(data, PostDTO.class);
		
		// log.info("Verificando se o usuário informado existe");
		// if (postRequestDTO.getUser().getLogin() == null ) {
		// 	response.getErrors().add("Usuário não encontrado.");			
		// }
		
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
		
		// if (UtilNumbers.isNumeric(postRequestDTO.getReacts() + ""))
		// 	post.setReacts(postRequestDTO.getReacts());

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
		
		///response.setData(postToDto(temp, anexos, null));
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


	// @PostMapping(value = "/drop-storage")
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

		Optional<List<Comment>> comment = commentService.findByPost(post.get());
		PostDTO dto = postToDto(post.get(), comment.get());
		dto.setReacts(getReacts(post.get()));

		response.setData(dto);
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



	private PostDTO postToDto(Post post, List<Comment> comments) {
		PostDTO dto = new PostDTO();
		
		dto.setType(post.getType().toString());
		dto.setId(post.getId());
		dto.setContent(post.getContent());
		dto.setTitle(post.getTitle());
		dto.setAnexo(post.getAnexo());

		List<Favorites> favorites = favoritesRepository.findAllByPost(post);
		favorites.forEach(f -> {
			dto.getFavorites().add(f.getUser().getLogin());
		});

		UserReductDTO user = userService.buscarUsuarioApiReduct(post.getLogin());      	
		dto.setUser(user);
        
		if (comments != null && !comments.isEmpty()) {
			comments = comments.stream()
			.sorted(Comparator.comparing(Comment::getTimestamp).reversed())
			.collect(Collectors.toList());

			comments.forEach(comment -> {
				dto.getComments().add(commentConverter.entityToDTO(post.getId(), user, comment));
			});			
		}
		return dto;
	}
	
	
	@GetMapping("/all/{login}")
	public ResponseEntity<Response<List<PostDTO>>> getAllByUser(@PathVariable("login") String login) {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAllByLogin(login);
		if (posts == null || posts.isEmpty()) {
			response.getErrors().add("Nenhum post encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		posts = posts.stream()
		.sorted(Comparator.comparing(Post::getCreateDate).reversed())
		.collect(Collectors.toList());

		for (Post post : posts) {
			Optional<List<Comment>> comment = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comment.get());
			dto.setReacts(getReacts(post));
			response.getData().add(dto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	@GetMapping()
	public ResponseEntity<Response<List<PostDTO>>> getAll() {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAll();
		if (posts == null || posts.isEmpty()) {
			response.getErrors().add("Nenhum post encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		posts = posts.stream()
		.sorted(Comparator.comparing(Post::getCreateDate).reversed())
		.collect(Collectors.toList());

		for (Post post : posts) {
			Optional<List<Comment>> comment = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comment.get());
			dto.setReacts(getReacts(post));
			response.getData().add(dto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	


	@GetMapping(value = "/search/{id}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<PostDTO>> searchById(@HeaderParam("x-access-token") Token token, @PathVariable Integer id, @RequestParam(name = "date", required = false) String date, @RequestParam(name = "user", required = false) String user) {
        
        Response<PostDTO> response = new Response<>();
        if (id == null) {
            response.getErrors().add("Id precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> optPost = postService.findById(id);
        if (!optPost.isPresent()) {
        	response.getErrors().add("Post não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        		
		String userSearch = optPost.get().getLogin();
		if (user != null && !user.isBlank()) {
			userSearch = user; 
		}
		
		Optional<User> optUser = null;
		Integer userId = null;
		UserRestModel userRestModel = null;
		if (token == null) {
			userRestModel = userService.carregarUsuariosApi(TokenService.getToken());
		} else {
			userRestModel = userService.carregarUsuariosApi(token.getToken());
		}

		if (userRestModel == null) {
			response.getErrors().add("Não foi possível carregar os usuários do endpoint");
			return ResponseEntity.badRequest().body(response); 
		}

		optUser = userService.findByLoginApi(userSearch, userRestModel.getOutput().getResult().getUsuarios());
		if (!optUser.isPresent()) {
			response.getErrors().add("Nenhum usuário encontrado");
			return ResponseEntity.badRequest().body(response);
		}
		userId = optUser.get().getId();
        
		optUser.get().setAvatar(userService.getAvatarBase64ByLogin(optUser.get().getLogin()));
        
        List<Comment> comments = genericRepositoryPostgres.searchCommentsByPostIdAndDateAndUser(new Post(id), date, optUser);

        PostDTO dto = postToDto(optPost.get(), comments);
        
		dto.setReacts(getReacts(optPost.get()));

        response.setData(dto);
        return ResponseEntity.ok().body(response);
	}
	
	
	private Map<String, List<String>> getReacts(Post post) {
		Map<String, List<String>> dto = new HashMap<>();
		
		List<React> reacts = reactRepository.findAll();
		reacts.forEach(react -> {
			dto.put(react.getReact(), new ArrayList<String>());
		});

		List<ReactUser> reactsUsers = reactUserRepository.findByPost(post);
		
		if (reactsUsers != null && !reactsUsers.isEmpty()) {
			reactsUsers.forEach(like -> {
				String login = like.getLogin();
				String react = like.getReact().getReact();
				if (dto.containsKey(react)) {
					dto.get(react).add(login);
				} else {
					dto.put(react, Arrays.asList(login));
				}
			});
		}
		return dto;
	} 
		
	
	///api/post/search/all?date=&user=
	@GetMapping(value = "/search/all")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<List<PostDTO>>> searchAll(@HeaderParam("x-access-token") Token token, @RequestParam(name = "date", required = false) String date, @RequestParam(name = "user", required = false) String user) {
        
        Response<List<PostDTO>> response = new Response<>();
        
		Optional<User> userOpt = Optional.empty();

		UserRestModel userRestModel = null;

		if (user != null && !user.isBlank()) {
			if (token == null) {
				userRestModel = userService.carregarUsuariosApi(TokenService.getToken());
			} else {
				userRestModel = userService.carregarUsuariosApi(token.getToken());
			}
			
			if (userRestModel == null) {
				response.getErrors().add("Não foi possível carregar os usuários do endpoint");
				return ResponseEntity.badRequest().body(response); 
			}

			userOpt = userService.findByLoginApi(user, userRestModel.getOutput().getResult().getUsuarios());
			if (!userOpt.isPresent()) {
				response.getErrors().add("Nenhum usuário encontrado");
				return ResponseEntity.badRequest().body(response);
			}			
		}
        
        List<Post> posts = genericRepositoryPostgres.searchPostsByDateOrUser(date, userOpt);

		// ordenar do mais novo pro mais antigo
		posts = posts.stream()
		.sorted(Comparator.comparing(Post::getCreateDate).reversed())
		.collect(Collectors.toList());

		List<PostDTO> postsDto = new ArrayList<>();
        
		for (Post p : posts) {
			// List<Comment> comms = genericRepositoryPostgres.searchCommentsByPostIdAndDateAndUser(p, date, userOpt);
			
			PostDTO dto = postToDto(p, null);
			// comms.forEach(c -> {
				// CommentDTO commDto = commentConverter.entityToDTO(c);
				// dto.getComments().add(commDto);
			// });
			
			dto.setReacts(getReacts(p));
			postsDto.add(dto);
		}


        response.setData(postsDto);
        return ResponseEntity.ok().body(response);
	}
	

	// /post/like/{id}/{login}
	@PutMapping("/like/{id}/{login}/{react}/{toggle}")
	public ResponseEntity<Response<PostDTO>> likes(@PathVariable("id") Integer idPost, @PathVariable("login") String login, @PathVariable("react") String react, @PathVariable("toggle") Boolean toggle) {
		Response<PostDTO> response = new Response<>();
		if (idPost == null) {
            response.getErrors().add("Id precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> optPost = postService.findById(idPost);
        if (!optPost.isPresent()) {
        	response.getErrors().add("Post não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
		
		Post post = optPost.get();

		if (reactUserRepository.existsByLoginAndPost(login, post)) {
		// if (!toggle) {
			ReactUser reactUser = reactUserRepository.findByLoginAndPost(login, post);
			reactUserRepository.deleteById(reactUser.getId());
			return ResponseEntity.ok().build();
		}
		
		React reactObj = reactRepository.findByReact(react);
		ReactUser reactUser = new ReactUser();
		reactUser.setLogin(login);
		reactUser.setPost(post);
		reactUser.setReact(reactObj);
		reactUser = reactUserRepository.save(reactUser);

		//post.getReacts().add(like);
		post = postService.persist(post);
		PostDTO dto = postToDto(post, null);
		dto.setReacts(getReacts(post));
		response.setData(dto);

		return ResponseEntity.ok().body(response);
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
