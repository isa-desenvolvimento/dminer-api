package com.dminer.dminer.services.interfaces;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.dminer.dminer.entities.Post;

public interface IFilesStorageService {

	
	/**
	 * Cria um novo diretório
	 */
	void init();
	
	/**
	 * Salva/Atualiza uma entidade
	 * @param Archive archive 
	 */
	boolean save(MultipartFile file, Path path);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Archive>
	 */
	Optional<Resource> findById(int id, Path path);
	
	/**
	 * Busca pelo Post
	 * @param int postId
	 * @return Optional<List<Archive>>
	 */
	Optional<Stream<Path>> findByPost(Post post, Path path);
	
	/**
	 * Deleta um arquivo
	 * @param int id
	 */
	void delete(Path path);
	
}
