package com.dminer.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dminer.dto.PostDTO;
import com.dminer.dto.PostReductDTO;
import com.dminer.dto.UserReductDTO;
import com.dminer.entities.Post;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;

@Service
public class FeedService {

	@Autowired
    private PostService postService;

	@Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;
	
	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;
	
	
	
    public List<PostDTO> getAll() {
        
        List<Post> posts = postService.findAll();        
        List<PostDTO> response = new ArrayList<>();
        
        if (posts.isEmpty()) {
        	return response;
        }
        posts.forEach(u -> {
        	response.add(postToDto(u));
        });        
        return response;
    }

    public List<PostReductDTO> getReductAll() {
        
        List<Post> posts = postService.findAll();        
        List<PostReductDTO> response = new ArrayList<>();
        
        if (posts.isEmpty()) {
        	return response;
        }
        posts.forEach(u -> {
        	response.add(postToReductDto(u));
        });
        return response;
    }
    
    
    public List<PostReductDTO> searchPostgres(String keyword) {
        
        List<Post> posts = genericRepositoryPostgres.searchPost(keyword);        
        List<PostReductDTO> response = new ArrayList<>();
        
        if (posts.isEmpty()) {
        	return response;
        }
        posts.forEach(u -> {
        	response.add(postToReductDto(u));
        });
        return response;
    }

    
    public List<PostReductDTO> searchSqlServer(String keyword) {
        
        List<Post> posts = genericRepositorySqlServer.searchPost(keyword);        
        List<PostReductDTO> response = new ArrayList<>();
        
        if (posts.isEmpty()) {
        	return response;
        }
        posts.forEach(u -> {
        	response.add(postToReductDto(u));
        });
        return response;
    }

    
    private PostDTO postToDto(Post post) {
    	PostDTO dto = new PostDTO();
		dto.setUser(new UserReductDTO(post.getLogin()));
		if (post.getType() != null)
			dto.setType(post.getType().name());
		dto.setId(post.getId());		
		dto.setContent(post.getContent());
		dto.setTitle(post.getTitle());
		return dto;
	}
    
    private PostReductDTO postToReductDto(Post post) {
    	PostReductDTO dto = new PostReductDTO();
    	dto.setIdPost(post.getId());
		dto.setContent(post.getContent());
		dto.setTitle(post.getTitle());
		return dto;
	}

    public List<PostReductDTO> search(String keyword, String login, boolean isProd) {
        List<Post> result = new ArrayList<>();
        if (keyword != null) {
            if (isProd) {
                result = genericRepositorySqlServer.searchPost(keyword);
            } else {
                result = genericRepositoryPostgres.searchPost(keyword);
            }
        } else {
            result = postService.findAllByLogin(login);
        }
                
        List<PostReductDTO> reduct = new ArrayList<>();
        for (Post post : result) {
            reduct.add(postToReductDto(post));
        }
        return reduct;
    }

}
