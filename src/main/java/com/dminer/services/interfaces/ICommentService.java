package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Comment;
import com.dminer.entities.Post;

public interface ICommentService {
    
    /**
	 * Salva/Atualiza um comentário
	 * @param Comment comment
	 * @return Optional<Comment>
	 */
	Comment persist(Comment comment);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Comment>
	 */
	Optional<Comment> findById(int id);
	
    /**
	 * Busca por post
	 * @param Post post
	 * @return Optional<Comment>
	 */
	List<Comment> findByPost(Post post);
	
	/**
	 * Deleta um comentário
	 * @param int id
	 */
	void delete(int id);

}
