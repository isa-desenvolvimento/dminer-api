package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.dto.UserDTO;
import com.dminer.entities.User;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;
import com.dminer.repository.UserRepository;
import com.dminer.services.interfaces.IUserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
	private UserRepository userRepository;	
	
    @Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;

    @Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;


	private static final Logger log = LoggerFactory.getLogger(UserService.class);


    @Override
    public User persist(User user) {
        log.info("Persistindo usuário: {}", user);
		return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(int id) {
        log.info("Buscando um usuário pelo id {}", id);
		return userRepository.findById(id);
    }

    @Override
    public Optional<List<User>> findAll() {
        log.info("Buscando todos os usuários");
		return Optional.ofNullable(userRepository.findAll());
    }

    @Override
    public void delete(int id) throws EmptyResultDataAccessException {
        log.info("Excluindo um usuário pelo id {}", id);
		userRepository.deleteById(id);
    }


    public Optional<List<UserDTO>> getBirthDaysOfMonth() {
        log.info("Buscando todos os usuários que fazem aniversário no mês");
		return Optional.ofNullable(genericRepositorySqlServer.getBirthDaysOfMonth());
    }
    
    public Optional<List<UserDTO>> getBirthDaysOfMonthPostgres() {
        log.info("[Postgres] Buscando todos os usuários que fazem aniversário no mês");
		return Optional.ofNullable(genericRepositoryPostgres.getBirthDaysOfMonth());
    } 
     
    
    public boolean existsByLogin(String login) {
        log.info("Verificando se usuário existe pelo login, {}", login);
        return userRepository.findByLogin(login) != null;
    }

    public Optional<User> findByLogin(String login) {
        log.info("Recuperando usuário pelo login, {}", login);
        return Optional.ofNullable(userRepository.findByLogin(login));
    }
    
}
