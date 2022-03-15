package com.dminer.services.interfaces;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.User;

public interface IUserService {
    
    /**
	 * Salva/Atualiza um usuário
	 * @param User user
	 * @return Optional<User>
	 */
	User persist(User user);
	
	/**
	 * Busca por id
	 * @param int id
	 * @return Optional<User>
	 */
	Optional<User> findById(int id);
	
    /**
	 * Busca todos os usuários
	 * @param int id
	 * @return Optional<List<User>>
	 */
	Optional<List<User>> findAll();
	
	/**
	 * Deleta um usuário
	 * @param int id
	 */
	void delete(int id);

}
