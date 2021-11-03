package com.dminer.repository;

import com.dminer.entities.FullCalendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FullCalendarRepository extends JpaRepository<FullCalendar, Integer> {
}
