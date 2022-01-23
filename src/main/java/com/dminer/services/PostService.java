package com.dminer.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.dminer.dto.PostDTO;
import com.dminer.dto.PostExternalApiDTO;
import com.dminer.entities.Comment;
import com.dminer.entities.Post;
import com.dminer.entities.User;
import com.dminer.repository.CommentRepository;
import com.dminer.repository.PostRepository;
import com.dminer.services.interfaces.IPostService;
import com.dminer.utils.UtilDataHora;

@Service
public class PostService implements IPostService {

	@Autowired
	private PostRepository postRepository;
	
	@Autowired
	private CommentRepository commentRepository;
	

	private static final Logger log = LoggerFactory.getLogger(PostService.class);
	
	
	@Override
	public Post persist(Post post) {
		log.info("Persistindo publicação: {}", post);
		return postRepository.save(post);
	}

	@Override
	public Optional<Post> findById(int id) {
		log.info("Buscando uma publicação pelo id {}", id);
		return postRepository.findById(id);
	}

	@Override
	public void delete(int id) {
		log.info("Excluindo uma publicação pelo id {}", id);
		postRepository.deleteById(id);		
	}

	public List<Post> findAll() {
		log.info("Buscando todas as publicações ");
		return sort(postRepository.findAll());
	}
	
	public List<Post> findAllByLogin(String login) {
		log.info("Buscando todas as publicações de {}", login);
		return sort(postRepository.findAllByLogin(login));
	}


	public HttpStatus salvarApiExterna(Post entity) {
		String url = "https://www.dminer.com.br/blog/wp-json/wp/v2/posts";
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic aW50cmFuZXQuZG1pbmVyOmpCRzQgS0RKayBGUHpiIHJTbmcgUGgwUiBpTkJQ");

		PostExternalApiDTO postExternal = new PostExternalApiDTO();
		postExternal.setContent(entity.getContent());
		postExternal.setTitle(entity.getTitle());

		HttpEntity<String> request = new HttpEntity<String>(postExternal.toJson(), headers);		
		ResponseEntity<String> out = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		return out.getStatusCode();
	}


	// public List<PostDTO> searchPostsAndCommentsByDateOrUser2(Timestamp date, User user) {
	// 	System.out.println(findById(1).get().toString());
	// 	return null;
	// }

	// public List<PostDTO> searchPostsAndCommentsByDateOrUser(Timestamp date, User user) {

    //     log.info("Buscando posts e comentários por data / usuário {}, {}", date, user);
    //     List<Post> posts = new ArrayList<PostDTO>();
	// 	if (user == null && date == null) {

	// 	}

	// 	List<Post> posts = findAll();
    //     if (posts == null) {
    //         return new ArrayList<PostDTO>();
    //     }

	// 	List<PostDTO> postsDto = new ArrayList<PostDTO>();

	// 	for (Post post : posts) {

	// 		List<Comment> comments = commentRepository.findByPost(post);


	// 		Predicate<Comment> condition1 = comment -> date != null && UtilDataHora.equals(comment.getTimestamp(), date);
	// 		Predicate<Comment> condition2 = comment -> date != null && UtilDataHora.equals(comment.getPost().getCreateDate(), date);
	// 		Predicate<Comment> condition3 = comment -> comment.getUser().getId() == user.getId();
	// 		Predicate<Comment> condition4 = comment -> comment.getPost().getLogin().equals(user.getLogin());
	
	// 		log.info("Comentários filtrados pela condicao 1");
	// 		comments = comments.stream()
	// 		.filter(condition1)
	// 		// .filter(condition1.or(condition2.or(condition3.or(condition4))))
	// 		.collect(Collectors.toList());
			
	// 		comments.forEach(c -> {
	// 			System.out.println("Comentário: " + c.getId() + "\t" + c.getTimestamp() + "\t" + c.getPost().getId());
	// 		});
	
	// 		postsDto = comments.stream()
	// 		.map(com -> com.getPost().convertDto())
	// 		.collect(Collectors.toList());
			
	// 		Set<PostDTO> set = new HashSet<PostDTO>(postsDto);
	// 		postsDto.clear();
	// 		postsDto.addAll(set);
			
	// 		for (PostDTO postDto : postsDto) {
	// 			if (comments != null) {
	// 				//addCommentInPostDto(comments, postDto);
	// 				for (Comment comment : comments) {
	// 					if (comment.getPost().getId() == post.getId()) {
	// 						postDto.getComments().add(comment.convertDto());
	// 						//comments.remove(comment);
	// 					}
	// 				}
	// 			}
	// 		}
	// 	}		
	// 	return postsDto;
    // }

	private PostDTO addCommentInPostDto(List<Comment> comm, PostDTO post) {
		for (Comment comment : comm) {
			if (comment.getPost().getId() == post.getId()) {
				post.getComments().add(comment.convertDto());
				comm.remove(comment);
			}
		}
		return post;
	}

	public List<Post> sort(List<Post> posts) {
		if (posts == null) {
			return new ArrayList<Post>();
		}

		posts = posts.stream()
			.sorted(Comparator.comparing(Post::getCreateDate).reversed())
			.collect(Collectors.toList());
		return posts;
	}

}
