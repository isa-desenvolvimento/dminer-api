package com.dminer.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.dminer.entities.Post;
import com.dminer.dminer.entities.abstracts.Archive;

public interface IArchiveService {

	/**
	 * Salva/Atualiza uma entidade
	 * @param Archive archive
	 * @return Optional<Archive>
	 */
	Archive persist(Archive archive);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Archive>
	 */
	Optional<Archive> findById(int id);
	
	/**
	 * Busca pelo Post
	 * @param int postId
	 * @return Optional<List<Archive>>
	 */
	Optional<List<Archive>> findByPost(Post post);
	
	/**
	 * Deleta um arquivo
	 * @param int id
	 */
	void delete(int id);	
	
}
