package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsTimeRepository extends JpaRepository<Events, Integer> {
    
    @Query(value= "SELECT * FROM Events WHERE year(end_date) = :year or year(start_date) = :year", nativeQuery = true)
    public List<Events> fetchEventsByYear(@Param("year") String year);

    @Query(value = "SELECT * FROM Events WHERE month(end_date) = :month or month(start_date) = :month " +
    "and year(end_date) = :year and year(start_date) = :year", nativeQuery = true)
    public List<Events> fetchEventsByMonth(@Param("year") String year, @Param("month") String month);

    @Query(value = "SELECT * FROM Events WHERE end_date = :date or start_date = :date", nativeQuery = true)
    public List<Events> fetchEventsByDate(@Param("date") String date);
    
    @Query(value = "SELECT * FROM Events WHERE start_date >= :dtInicio and end_date <= :dtFim", nativeQuery = true)
    public List<Events> fetchEventsInBetween(String dtInicio, String dtFim);

}
