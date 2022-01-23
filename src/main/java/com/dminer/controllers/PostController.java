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
import com.dminer.entities.FileInfo;
import com.dminer.entities.ReactUser;
import com.dminer.entities.Post;
import com.dminer.entities.React;
import com.dminer.entities.User;
import com.dminer.enums.PostType;
import com.dminer.repository.CommentRepository;
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
	private GenericRepositoryPostgres genericRepositoryPostgres;


	@PostMapping()
	public ResponseEntity<Response<PostDTO>> create(@RequestBody PostRequestDTO dto, BindingResult result) {
	
		Response<PostDTO> response = new Response<>();
		
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
		if (post.getType().equals(PostType.EXTERNAL)) {
			// salvar na api externa
			HttpStatus code = postService.salvarApiExterna(post);
			if (code.value() != 201) {
				return ResponseEntity.internalServerError().body(null);
			}
		}

		response.setData(postToDto(post, null, null));
		return ResponseEntity.status(201).body(response);
	}
	

	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Response<String>> delete(@PathVariable("id") int id) {
		
		Response<String> response = new Response<>();

		Optional<Post> post = postService.findById(id);
		if (post.isPresent()) {
			log.info("Deletando Post {}", post.get());

			List<Comment> comments = commentService.findByPost(post.get());
			if (!comments.isEmpty()) {
				comments.forEach(comment -> {
			 		commentService.delete(comment.getId());
			 	});
			}
			
			postService.delete(post.get().getId());
			response.setData("Post deletado!");

		} else {
			response.addError("Post não encontrado");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		return ResponseEntity.ok().body(response);
	}


	@GetMapping(value = "/{id}")
	public ResponseEntity<Response<PostDTO>> get(@HeaderParam("x-access-token") Token token, @PathVariable("id") int id) {
		
		Response<PostDTO> response = new Response<>();
		log.info("Recuperando Post {}", id);

		Optional<Post> post = postService.findById(id);
		if (!post.isPresent()) {
			response.addError("Post não encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		List<Comment> comment = commentService.findByPost(post.get());
		PostDTO dto = postToDto(post.get(), comment, token);
		response.setData(dto);

		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	private PostDTO postToDto(Post post, List<Comment> comments, Token token) {
		PostDTO dto = new PostDTO();

		UserReductDTO user = new UserReductDTO();
		if (token != null)
			user = userService.buscarUsuarioApiReduct(post.getLogin(), token.getToken());
        
		dto = post.convertDto(user, comments);
		dto.setReacts(getReacts(post));

		return dto;
	}


	private PostDTO postToDto(Post post, List<Comment> comments) {
		PostDTO dto = new PostDTO();
        dto.setId(post.getId());
		comments.forEach(comm -> {
			dto.getComments().add(new CommentDTO(comm.getId(), null, null, null, null));
		});
		return dto;
	}
	
	
	@GetMapping("/all/{login}")
	public ResponseEntity<Response<List<PostDTO>>> getAllByUser(@HeaderParam("x-access-token") Token token, @PathVariable("login") String login) {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAllByLogin(login);
		if (posts == null || posts.isEmpty()) {
			response.addError("Nenhum post encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		for (Post post : posts) {
			List<Comment> comment = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comment, token);		
			response.getData().add(dto);
		}
		return ResponseEntity.ok().body(response);
	}
	
	
	@GetMapping()
	public ResponseEntity<Response<List<PostDTO>>> getAll(@HeaderParam("x-access-token") Token token) {
		
		Response<List<PostDTO>> response = new Response<>();
		response.setData(new ArrayList<PostDTO>());
		log.info("Recuperando todos os Post");

		List<Post> posts = postService.findAll();
		if (posts == null || posts.isEmpty()) {
			response.addError("Nenhum post encontrado na base de dados");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		for (Post post : posts) {
			List<Comment> comments = commentService.findByPost(post);
			PostDTO dto = postToDto(post, comments, token);
			response.getData().add(dto);
		}
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}


	@GetMapping(value = "/search/{id}")
    @Transactional(timeout = 50000)
    public ResponseEntity<Response<PostDTO>> searchById(@HeaderParam("x-access-token") Token token, @PathVariable Integer id, @RequestParam(name = "date", required = false) String date, @RequestParam(name = "user", required = false) String user) {
        
        Response<PostDTO> response = new Response<>();
        if (id == null) {
            response.addError("Id precisa ser informado");
            return ResponseEntity.badRequest().body(response);
        }

		Optional<Post> post = postService.findById(id);
        if (!post.isPresent()) {
        	response.addError("Post não encontrado");
            return ResponseEntity.badRequest().body(response);
        }
        
		String userSearch = post.get().getLogin();
		if (user != null && !user.isBlank()) {
			userSearch = user;
		}
		
		Optional<User> optUser = null;

		optUser = userService.findByLogin(userSearch);
		if (!optUser.isPresent()) {
			response.addError("Nenhum usuário encontrado");
			return ResponseEntity.badRequest().body(response);
		}

		List<Comment> comments = new ArrayList<>();
		Timestamp time = date != null ? UtilDataHora.toTimestamp(date) : null;

        comments = genericRepositoryPostgres.searchCommentsByPostIdAndDateAndUser(new Post(id), date, optUser);
		// comments = commentService.searchCommentsByPostIdAndDateAndUser(new Post(id), time, optUser.get());
		
        // PostDTO dto = postToDto(post.get(), comments, token);
		PostDTO dto = postToDto(post.get(), comments);
        
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

		if (user != null && !user.isBlank()) {
			userOpt = userService.findByLoginApi(user, token.getToken());
			if (!userOpt.isPresent()) {
				response.addError("Nenhum usuário encontrado");
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
			
			PostDTO dto = postToDto(p, null, null);
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
	@PutMapping("/like/{id}/{login}/{react}/{likeBo}")
	public ResponseEntity<Response<PostDTO>> likes(@PathVariable("id") Integer idPost, @PathVariable("login") String login, @PathVariable("react") String react, @PathVariable("likeBo") Boolean like) {
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
		
		Post post = optPost.get();

		//if (reactUserRepository.existsByLoginAndPost(login, post)) {
		if (!like) {
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

		post = postService.persist(post);
		PostDTO dto = postToDto(post, null, null);
		response.setData(dto);

		return ResponseEntity.ok().body(response);
	}

	

}
