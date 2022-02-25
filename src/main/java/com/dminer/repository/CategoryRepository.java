package com.dminer.repository;

import java.util.Optional;

import com.dminer.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByName(String name);

    Boolean existsByName(String name);
}
