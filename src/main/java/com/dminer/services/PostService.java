package com.dminer.services;

import java.util.List;
import java.util.Optional;

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

import com.dminer.dto.PostExternalApiDTO;
import com.dminer.entities.Post;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.PostRepository;
import com.dminer.services.interfaces.IPostService;

@Service
public class PostService implements IPostService {

	@Autowired
	private PostRepository postRepository;	

	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;	

	@Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;


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
		return p;
	}

	@Override
	public void delete(int id) {
		log.info("Excluindo uma publicação pelo id {}", id);
		postRepository.deleteById(id);		
	}

	public List<Post> findAll() {
		log.info("Buscando todas as publicações ");
		List<Post> p = postRepository.findAllByOrderByCreateDateDesc();
		log.info("{} publicações encontradas", p.size());
		return p;
	}
	
	public List<Post> findAllByLogin(String login) {
		log.info("Buscando todas as publicações de {}", login);
		List<Post> p = postRepository.findAllByLoginOrderByCreateDateDesc(login);
		return p;
	}

	public List<Post> search(String keyword, boolean isProd) {
		log.info("Search em posts o termo: {}", keyword);
		if (keyword == null) {
			return findAll();
		}

		if (isProd) {
			return genericRepositorySqlServer.searchPost(keyword);
		}		
		return genericRepositoryPostgres.searchPost(keyword);
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

}
