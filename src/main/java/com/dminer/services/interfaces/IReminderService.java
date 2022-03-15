package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Reminder;

public interface IReminderService {
    
    /**
	 * Salva/Atualiza um lembrete
	 * @param Reminder reminder
	 * @return Optional<Reminder>
	 */
	Reminder persist(Reminder reminder);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Reminder>
	 */
	Optional<Reminder> findById(int id);

    /**
	 * Busca todas os lembretes
	 * @return Optional<Reminder>
	 */
	Optional<List<Reminder>> findAll();
		
	/**
	 * Deleta um lembrete
	 * @param int id
	 */
	void delete(int id);

}
