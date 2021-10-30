package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Notice;

public interface INoticeService {
    
    /**
	 * Salva/Atualiza um comentário
	 * @param Notice comment
	 * @return Optional<Avisos>
	 */
	Notice persist(Notice comment);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Avisos>
	 */
	Optional<Notice> findById(int id);
	
    /**
	 * Busca todos
	 * @param Post post
	 * @return Optional<Avisos>
	 */
	Optional<List<Notice>> findAll();
	
	/**
	 * Deleta um comentário
	 * @param int id
	 */
	void delete(int id);

}
