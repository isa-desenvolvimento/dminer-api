package com.dminer.repository;


import com.dminer.entities.Benefits;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BenefitsRepository extends JpaRepository<Benefits, Integer> {
    
}
