package com.dminer.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.dminer.entities.Benefits;
import com.dminer.repository.GenericRepositoryPostgres;
import com.dminer.repository.GenericRepositorySqlServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class BenefitsService {
    
    @Autowired
    private Environment env;

    @Autowired
	private GenericRepositorySqlServer genericRepositorySqlServer;
	
	@Autowired
	private GenericRepositoryPostgres genericRepositoryPostgres;

    
    public List<Benefits> search(String keyword) {
        List<Benefits> entities = new ArrayList<>();
        if (isProd()) {
            entities = genericRepositorySqlServer.searchBenefits(keyword);
        } else {
        	entities = genericRepositoryPostgres.searchBenefits(keyword);
        }

        entities = entities.stream()
		.sorted(Comparator.comparing(Benefits::getDate).reversed())
		.collect(Collectors.toList());
        
        return entities;
    }

    public boolean isProd() {
        return Arrays.asList(env.getActiveProfiles()).contains("prod");
    }
}
