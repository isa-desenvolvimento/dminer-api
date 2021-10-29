package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Avisos;
import com.dminer.entities.Post;

public interface IAvisosService {
    
    /**
	 * Salva/Atualiza um comentário
	 * @param Avisos comment
	 * @return Optional<Avisos>
	 */
	Avisos persist(Avisos comment);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Avisos>
	 */
	Optional<Avisos> findById(int id);
	
    /**
	 * Busca todos
	 * @param Post post
	 * @return Optional<Avisos>
	 */
	Optional<List<Avisos>> findAll();
	
	/**
	 * Deleta um comentário
	 * @param int id
	 */
	void delete(int id);

}
