package com.dminer.controllers;

import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dminer.constantes.Constantes;
import com.dminer.converters.CommentConverter;
import com.dminer.dto.PostDTO;
import com.dminer.dto.PostRequestDTO;
import com.dminer.dto.Token;
import com.dminer.dto.UserDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Favorites;
import com.dminer.entities.FileInfo;
import com.dminer.entities.Notification;
import com.dminer.entities.ReactUser;
import com.dminer.entities.Post;
import com.dminer.entities.React;
import com.dminer.entities.User;
import com.dminer.enums.PostType;
import com.dminer.repository.FavoritesRepository;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.ReactRepository;
import com.dminer.repository.ReactUserRepository;
import com.dminer.response.Response;
import com.dminer.rest.model.users.UserAvatar;
import com.dminer.rest.model.users.UserRestModel;
import com.dminer.services.CommentService;
import com.dminer.services.FileDatabaseService;
import com.dminer.services.FileStorageService;
import com.dminer.services.NotificationService;
import com.dminer.services.PostService;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

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
	private ReactRepository reactRepository;

	@Autowired
	private ReactUserRepository reactUserRepository;
	
	@Autowired
	private CommentConverter commentConverter;    
	
	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;
	
	@Autowired
	private FavoritesRepository favoritesRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
    private Environment env;


	@PostMapping()
	public ResponseEntity<Response<PostDTO>> create(@RequestHeader("x-access-token") Token token, @RequestBody PostRequestDTO dto, BindingResult result) {
	
		Response<PostDTO> response = new Response<>();
		validateRequestDto(dto, result);
		if (result.hasErrors()) {
            log.info("Erro validando PostRequestDTO: {}", dto);
            response.addErrors(result);
            return ResponseEntity.badRequest().body(response);
        }
		
		Optional<User> userOpt = userService.findByLogin(dto.getLogin());
		UserDTO userDto = null;
		if (! userOpt.isPresent()) {
			userDto = userService.buscarUsuarioApi(dto.getLogin(), token.getToken());
			userService.persist(new User(userDto.getLogin(), userDto.getUserName()));
		} 

		Post post = new Post();
		post.setAnexo(dto.getAnexo());
		post.setContent(dto.getContent());		
		post.setLogin(dto.getLogin());
		post.setTitle(dto.getTitle());
		if (dto.getType() == 1) {
			post.setType(PostType.INTERNAL);
		} else {
			post.setType(PostType.EXTERNAL);
		}

		post = postService.persist(post);
		PostDTO dtoPost = postToDto(post, null, false, false, null);
		
		UserReductDTO userReduct = userDto.toReductDto();
		
		dtoPost.setUser(userReduct);
		dtoPost.setReacts(getReacts(post));
		response.setData(dtoPost);	

		if (post.getType().equals(PostType.EXTERNAL)) {
			// salvar na api externa
			HttpStatus code = postService.salvarApiExterna(post);
			if (code.value() != 201) {
				return ResponseEntity.internalServerError().body(null);
			}
		}

		// salvarNotificacoes(token, dto);

		Notification notification = new Notification();
		notification.setCreateDate(Timestamp.from(Instant.now()));
		notification.setNotification("Usuário " + dto.getLogin() + " fez um novo post! - " + post.getTitle() + ":" + post.getContent());
		notification.setAllUsers(true);
		notificationService.persist(notification);

		return ResponseEntity.ok().body(response);
	}
	
	@Async
	private void salvarNotificacoes(Token token, PostRequestDTO dto) {
		log.info("Salvando notificações de forma assíncrona ");
		List<UserReductDTO> usuariosApi = userService.carregarUsuariosApiReductDto(token.getToken(), false);
		if (!usuariosApi.isEmpty()) {
			usuariosApi.forEach(usuario -> {
				Notification notification = new Notification();
				notification.setCreateDate(Timestamp.from(Instant.now()));
	            notification.setNotification("Usuário " + dto.getLogin() + " fez um novo post!");
            	Optional<User> userTemp = userService.findByLogin(usuario.getLogin());
				if (userTemp.isPresent()) {
					notification.setUser(userTemp.get());
					notificationService.persist(notification);
				} else {
					log.info("Usuário {} não encontrado na base de dados local", usuario);
				}
			});
		}
		log.info("Fim salvando notificações de forma assíncrona ");
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
				response.addError("Diretório do Post não foi deletado corretamente");
				return ResponseEntity.internalServerError().body(response);
			}			
		} else {
			response.addError("Post não encontrado");
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
	public ResponseEntity<Response<PostDTO>> get(@RequestHeader("x-access-token") Token token, @PathVariable("id") int id) {
		
		Response<PostDTO> response = new Response<>();
		log.info("Recuperando Post {}", id);

		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> post = postService.findById(id);
		if (!post.isPresent()) {
			response.addError("Post não encontrado na base de dados");
			return ResponseEntity.ok().body(response);
		}

		Optional<List<Comment>> comment = commentService.findByPost(post.get());
		PostDTO dto = postToDto(post.get(), comment.get(), true, true, null);
		String avatarPost = getAvatarByPost(post.get());
		dto.getUser().setAvatar(avatarPost);

		response.setData(dto);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	private String getAvatarByPost(Post post) {
		return userService.getAvatarEndpoint(post.getLogin());
	}

	// @GetMapping(value = "/{id}")
	// public ResponseEntity<Response<PostDTO>> get(@RequestHeader("x-access-token") Token token, @PathVariable("id") int id) {
		
	// 	Response<PostDTO> response = new Response<>();
	// 	log.info("Recuperando Post {}", id);

	// 	if (token.naoPreenchido()) { 
    //         response.addError("Token precisa ser informado");    		
    // 		return ResponseEntity.badRequest().body(response);
    //     }

	// 	Optional<Post> post = postService.findById(id);
	// 	if (!post.isPresent()) {
	// 		response.addError("Post não encontrado na base de dados");
	// 		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	// 	}

	// 	Optional<List<Comment>> comment = commentService.findByPost(post.get());
	// 	UserReductDTO userReductDTO = userService.findByLogin(post.get().getLogin()).get().convertReductDto();

	// 	PostDTO dto = postToDto2(post.get(), comment.get(), userReductDTO);
	// 	dto.setReacts(getReacts(post.get()));

	// 	response.setData(dto);
	// 	return ResponseEntity.status(HttpStatus.OK).body(response);
	// }



	/**
	 * todo -> Refatorar
	 */
	private PostDTO postToDto(Post post, List<Comment> comments, boolean usuario, boolean reacts, UserRestModel<UserAvatar> allAvatarCustomer) {
		PostDTO postDto = new PostDTO();
		
		postDto.setType(post.getType().toString());
		postDto.setId(post.getId());
		postDto.setContent(post.getContent());
		postDto.setTitle(post.getTitle());
		postDto.setAnexo(post.getAnexo());
		postDto.setDateCreated(UtilDataHora.dateToFullStringUTC(post.getCreateDate()));
		
		List<Favorites> favorites = favoritesRepository.findAllByPost(post);
		favorites.forEach(f -> {
			postDto.getFavorites().add(f.getUser().getLogin());
		});

		Optional<User> user = userService.findByLogin(post.getLogin());

		/**
		 * Se usuário é true, trazer o usuário do post no formato UserReductDto
		 */
		if (usuario) {
			if (user.isPresent()) {
				postDto.setUser(user.get().convertReductDto());
			}
		}

		/**
		 * Se a coleção de avatares vier preenchida, fazer a busca dos avatares tanto de post quanto de comentários, pela coleção já carregada
		 */
		if (allAvatarCustomer != null) {
			String avatarPost = getAvatarByUsername(allAvatarCustomer, user.get().getUserName());
			postDto.getUser().setAvatar(avatarPost);

			if (comments != null && !comments.isEmpty()) {
				comments.forEach(comment -> {
					postDto.getComments().add(commentConverter.entityToDto(comment, allAvatarCustomer));
				});			
			}

		} else {
			
			/**
			 * Se não, buscar o avatar um a um, tanto para o post quanto para os comentários
			 */
			String avatarPost = getAvatarByPost(post);
			postDto.getUser().setAvatar(avatarPost);

			if (comments != null && !comments.isEmpty()) {
				comments.forEach(comment -> {
					postDto.getComments().add(commentConverter.entityToDto(comment));
				});			
			}			
		}

		if (reacts) {
			postDto.setReacts(getReacts(post));
		}
		return postDto;
	}
	

	
	@GetMapping("/all/{login}")
	public ResponseEntity<Response<List<PostDTO>>> getAllByUser(@RequestHeader("x-access-token") Token token, @PathVariable("login") String login) {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		List<Post> posts = postService.findAllByLogin(login);
		if (posts == null || posts.isEmpty()) {
			response.addError("Nenhum post encontrado na base de dados");
			return ResponseEntity.ok().body(response);
		}
		
		UserRestModel<UserAvatar> allAvatarCustomer = userService.getAllAvatarCustomer(token.getToken());

		for (Post post : posts) {
			Optional<List<Comment>> comment = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comment.get(), true, true, allAvatarCustomer);
			response.getData().add(dto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	@GetMapping("/all")
	public ResponseEntity<Response<List<PostDTO>>> getAll(@RequestHeader("x-access-token") Token token) {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAll();
		if (posts == null || posts.isEmpty()) {
			response.addError("Nenhum post encontrado na base de dados");
			return ResponseEntity.ok().body(response);
		}

		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		UserRestModel<UserAvatar> allAvatarCustomer = userService.getAllAvatarCustomer(token.getToken());

		for (Post post : posts) {			
			Optional<List<Comment>> comment = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comment.get(), true, true, allAvatarCustomer);
			response.getData().add(dto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}




	@GetMapping(value = "/search/{id}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<PostDTO>> searchById(@RequestHeader("x-access-token") Token token, @PathVariable Integer id, @RequestParam(name = "date", required = false) String date, @RequestParam(name = "user", required = false) String user) {
        
        Response<PostDTO> response = new Response<>();
        if (id == null) {
            response.addError("Id precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> optPost = postService.findById(id);
        if (!optPost.isPresent()) {
        	response.addError("Post não encontrado");
            return ResponseEntity.ok().body(response);
        }
        		
		String userSearch = optPost.get().getLogin();
		if (user != null && !user.isBlank()) {
			userSearch = user; 
		}
				
		Optional<User> optUser = userService.findByLogin(userSearch);
		if (!optUser.isPresent()) {
			response.addError("Nenhum usuário encontrado");
			return ResponseEntity.ok().body(response);
		}
        
		optUser.get().setAvatar(userService.getAvatarEndpoint(optUser.get().getLogin()));
        
        List<Comment> comments = genericRepositoryPostgres.searchCommentsByPostIdAndDateAndUser(new Post(id), date, optUser);
		
        PostDTO dto = postToDto(optPost.get(), comments, true, true, null);
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
    public ResponseEntity<Response<List<PostDTO>>> searchAll(@RequestHeader("x-access-token") Token token, @RequestParam(name = "date", required = false) String date, @RequestParam(name = "user", required = false) String user) {
        
        Response<List<PostDTO>> response = new Response<>();        
		Optional<User> userOpt = Optional.empty();

		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		if (user != null && !user.isBlank()) {			
			userOpt = userService.findByLogin(user);
			if (!userOpt.isPresent()) {
				response.addError("Nenhum usuário encontrado");
				return ResponseEntity.ok().body(response);
			}			
		}
        
        List<Post> posts = genericRepositoryPostgres.searchPostsByDateOrUser(date, userOpt);
		List<PostDTO> postsDto = new ArrayList<>();
        
		UserRestModel<UserAvatar> allAvatarCustomer = userService.getAllAvatarCustomer(token.getToken());

		for (Post p : posts) {
			PostDTO dto = postToDto(p, null, false, true, allAvatarCustomer);
			postsDto.add(dto);
		}

        response.setData(postsDto);
        return ResponseEntity.ok().body(response);
	}
	

	// /post/like/{id}/{login}
	@PutMapping("/like/{id}/{login}/{react}/{toggle}")
	public ResponseEntity<Response<PostDTO>> likes(@RequestHeader("x-access-token") Token token, @PathVariable("id") Integer idPost, @PathVariable("login") String login, @PathVariable("react") String react, @PathVariable("toggle") Boolean toggle) {
		Response<PostDTO> response = new Response<>();
		if (idPost == null) {
            response.addError("Id precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> optPost = postService.findById(idPost);
        if (!optPost.isPresent()) {
        	response.addError("Post não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
		
		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
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
		PostDTO dto = postToDto(post, null, true, true, null);
		response.setData(dto);

		return ResponseEntity.ok().body(response);
	}


	@GetMapping(value = "/search/{keyword}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<List<PostDTO>>> searchAll(@RequestHeader("x-access-token") Token token, @RequestParam(name = "keyword", required = false) String keyword) {
        
        Response<List<PostDTO>> response = new Response<>();
        
		if (token.naoPreenchido()) { 
            response.addError("Token precisa ser informado");    		
    		return ResponseEntity.badRequest().body(response);
        }

		List<Post> posts = postService.search(keyword, isProd());
		List<PostDTO> postsDto = new ArrayList<>();
        
		UserRestModel<UserAvatar> allAvatarCustomer = userService.getAllAvatarCustomer(token.getToken());

		for (Post p : posts) {
			Optional<List<Comment>> comment = commentService.findByPost(p);
			PostDTO dto = postToDto(p, comment.get(), true, true, allAvatarCustomer);
			postsDto.add(dto);
		}

        response.setData(postsDto);
        return ResponseEntity.ok().body(response);
	}


	private String getAvatarByUsername(UserRestModel<UserAvatar> usuarios, String userName) {
		UserAvatar userAvatar = usuarios.getUsers().stream().filter(usuario -> 
			usuario.getUserName().equals(userName)
		).findFirst().orElse(null);
		if (userAvatar == null || userAvatar.isCommonAvatar()) {
			return "data:image/png;base64," + usuarios.getOutput().getResult().getCommonAvatar();
		}
		return "data:image/png;base64," + userAvatar.getAvatar();
	}
	
	
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

		if (dto.getAnexo() == null) {
			dto.setAnexo("");
		}
        
    }

	public boolean isProd() {
        log.info("ambiente: " + env.getActiveProfiles()[0]);
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }

}
