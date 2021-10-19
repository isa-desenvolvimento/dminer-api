package com.dminer.dminer.services;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dminer.dminer.entities.Post;
import com.dminer.dminer.repository.PostRepository;
import com.dminer.dminer.services.interfaces.IPostService;

public class PostService implements IPostService {

	@Autowired
	private PostRepository postRepository;	
	
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
	
}
