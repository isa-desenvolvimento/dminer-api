package com.dminer.repository;

import com.dminer.entities.Events;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventsTimeRepository extends JpaRepository<Events, Integer> {
    
}
