package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Notification;

public interface INotificationService {
    
    /**
	 * Salva/Atualiza uma notificação
	 * @param Notification notification
	 * @return Optional<Notification>
	 */
	Notification persist(Notification notification);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<Notification>
	 */
	Optional<Notification> findById(int id);

    /**
	 * Busca todas as notificações
	 * @return Optional<Notification>
	 */
	Optional<List<Notification>> findAll();
		
	/**
	 * Deleta uma notificação
	 * @param int id
	 */
	void delete(int id);

}
