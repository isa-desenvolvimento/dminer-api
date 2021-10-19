package com.dminer.dminer.services.interfaces;

import java.util.Optional;

import com.dminer.dminer.entities.Post;

public interface IPostService {
	
	/**
	 * Salva/Atualiza uma publicação
	 * @param Post post
	 * @return Optional<Post>
	 */
	Post persist(Post post);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Post>
	 */
	Optional<Post> findById(int id);
	
	
	/**
	 * Deleta uma publicação
	 * @param int id
	 */
	void delete(int id);
	
}
