package com.dminer.repository;

import java.util.List;

import com.dminer.entities.FullCalendar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FullCalendarRepository extends JpaRepository<FullCalendar, Integer> {

    @Query("select f from FullCalendar f where f.end > current_date()")
    List<FullCalendar> findAllActives();
}
