package com.dminer.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.dminer.entities.FileInfo;
import com.dminer.dminer.entities.Post;

public interface IFileDatabaseService {

	
	/**
	 * Salva/Atualiza uma entidade FileInfo
	 * @param FileInfo archive 
	 */
	Optional<FileInfo> persist(FileInfo file);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<FileInfo>
	 */
	Optional<FileInfo> findById(int id);
	
	/**
	 * Busca pelo Post
	 * @param Post post
	 * @return List<FileInfo>
	 */
	Optional<List<FileInfo>> findByPost(Post post);
	
	/**
	 * Deleta um arquivo
	 * @param int id
	 */
	void delete(int id);
}
