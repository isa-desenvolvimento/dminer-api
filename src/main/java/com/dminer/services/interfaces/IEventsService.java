package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Events;

public interface IEventsService {

    /**
	 * Salva/Atualiza um evento
	 * @param Events events
	 * @return Optional<Events>
	 */
	Events persist(Events events);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Events>
	 */
	Optional<Events> findById(int id);
	
	/**
	 * Busca todos os eventos
	 * @param int id
	 * @return Optional<List<Events>>
	 */
	Optional<List<Events>> findAll();
    
	/**
	 * Deleta um evento
	 * @param int id
	 */
	void delete(int id);
    
}
