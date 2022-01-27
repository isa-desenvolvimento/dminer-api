package com.dminer.validadores;

import com.dminer.repository.BenefitsRepository;
import com.dminer.repository.PermissionRepository;
import com.dminer.repository.PostRepository;
import com.dminer.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Validators {
    
    private static final Logger log = LoggerFactory.getLogger(Validators.class);

    @Autowired
    private BenefitsRepository benefitsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    public boolean existsUserByLogin(String login) {
        log.info("Verificando se User existe pelo login, {}", login);
        return userRepository.existsByLogin(login);
    }
    
    public boolean existsBenefitsById(Integer id) {
        log.info("Verificando se Benefits existe pelo id, {}", id);
        return benefitsRepository.existsById(id);
    }

    public boolean existsPermissionById(Integer id) {
        log.info("Verificando se Permission existe pelo id, {}", id);
        return permissionRepository.existsById(id);
    }

    public boolean existsPostById(Integer id) {
        log.info("Verificando se Post existe pelo id, {}", id);
        return postRepository.existsById(id);
    }
}
