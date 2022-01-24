package com.dminer.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.dminer.entities.Favorites;
import com.dminer.entities.Post;
import com.dminer.repository.FavoritesRepository;
import com.dminer.repository.PostRepository;
import com.dminer.services.interfaces.IPostService;

@Service
public class PostService implements IPostService {

	@Autowired
	private PostRepository postRepository;	

	@Autowired
	private FavoritesRepository favoritesRepository;

	
	private static final Logger log = LoggerFactory.getLogger(PostService.class);
	
	
	@Override
	public Post persist(Post post) {
		log.info("Persistindo publicação: {}", post);
		return postRepository.save(post);
	}

	@Override
	public Optional<Post> findById(int id) {
		log.info("Buscando uma publicação pelo id {}", id);
		Optional<Post> p = postRepository.findById(id);
		// if (p.isPresent()) {
		// 	List<Favorites> favs = carregarFavoritos(p.get());
		// 	p.get().setFavorites(favs);
		// }
		return p;
	}

	@Override
	public void delete(int id) {
		log.info("Excluindo uma publicação pelo id {}", id);
		postRepository.deleteById(id);		
	}

	public List<Post> findAll() {
		log.info("Buscando todas as publicações ");
		List<Post> p = postRepository.findAll();
		// if (p != null && !p.isEmpty()) {
		// 	p.forEach(post -> {
		// 		List<Favorites> favs = carregarFavoritos(post);
		// 		post.setFavorites(favs);
		// 	});
		// }
		return p;
	}
	
	public List<Post> findAllByLogin(String login) {
		log.info("Buscando todas as publicações de {}", login);
		List<Post> p = postRepository.findAllByLogin(login);
		// if (p != null && !p.isEmpty()) {
		// 	p.forEach(post -> {
		// 		List<Favorites> favs = carregarFavoritos(post);
		// 		post.setFavorites(favs);
		// 	});
		// }
		return p;
	}

	// public List<Favorites> carregarFavoritos(Post post) {
	// 	List<Favorites> favs = favoritesRepository.findAllByPost(post);
	// 	if (favs.isEmpty()) {
	// 		return new ArrayList<>();
	// 	}
	// 	return favs;
	// }

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

	// public String getToken() {
    // 	String uri = "https://www.dminerweb.com.br:8553/api/auth/login";
    // 	RestTemplate restTemplate = new RestTemplate();
    // 	HttpHeaders headers = new HttpHeaders();    	
    // 	headers.setContentType(MediaType.APPLICATION_JSON);
    // 	JSONObject personJsonObject = new JSONObject();
    //     personJsonObject.put("userName", "matheus.ribeiro1");
    //     personJsonObject.put("userPassword", "#Matheus97");
    //     HttpEntity<String> request = new HttpEntity<String>(personJsonObject.toString(), headers);
        
    //     String personResultAsJsonStr = restTemplate.postForObject(uri, request, String.class);
    //     JSONObject retorno = new JSONObject(personResultAsJsonStr);
    //     return (String) retorno.get("baererAuthentication");
    // }
}
