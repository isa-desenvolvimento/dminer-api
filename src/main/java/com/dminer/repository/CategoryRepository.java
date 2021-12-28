package com.dminer.repository;

import java.util.Optional;

import com.dminer.entities.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    
    Optional<Category> findByTitle(String title);

    Boolean existsByTitle(String title);
}
