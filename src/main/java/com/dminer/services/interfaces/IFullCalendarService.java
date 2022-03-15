package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.FullCalendar;

public interface IFullCalendarService {

    /**
	 * Salva/Atualiza um calendario
	 * @param FullCalendar fullCalendar
	 * @return Optional<FullCalendar>
	 */
	FullCalendar persist(FullCalendar fullCalendar);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<FullCalendar>
	 */
	Optional<FullCalendar> findById(int id);
	
	/**
	 * Busca todos os calendarios
	 * @param int id
	 * @return Optional<List<FullCalendar>>
	 */
	Optional<List<FullCalendar>> findAll();
    
	/**
	 * Deleta um calendario
	 * @param int id
	 */
	void delete(int id);
    
}
