package com.dminer.services;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Avisos;
import com.dminer.entities.Post;
import com.dminer.repository.AvisosRepository;
import com.dminer.services.interfaces.IAvisosService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AvisosService implements IAvisosService {

    @Autowired
    private AvisosRepository avisosRepository;

    @Override
    public Avisos persist(Avisos comment) {
        return avisosRepository.save(comment);
    }

    @Override
    public Optional<Avisos> findById(int id) {
        return avisosRepository.findById(id);
    }

    @Override
    public Optional<List<Avisos>> findAll() {
        return Optional.ofNullable(avisosRepository.findAll());
    }

    @Override
    public void delete(int id) {
        avisosRepository.deleteById(id);
    }
    
}
