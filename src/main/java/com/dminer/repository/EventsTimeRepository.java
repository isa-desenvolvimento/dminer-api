package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsTimeRepository extends JpaRepository<Events, Integer> {
    
   
    //sql server -> dev
    @Query(value= "SELECT * FROM Events WHERE year(end_date) = :year or year(start_date) = :year", nativeQuery = true)
    public List<Events> fetchEventsByYearSqlServer(@Param("year") String year);

    @Query(value = "SELECT * FROM Events WHERE start_date between :date and :dateHour", nativeQuery = true)
    public List<Events> fetchEventsByDateSqlServer(@Param("date") String date, @Param("dateHour") String dateHour);
    
    @Query(value = "SELECT * FROM Events WHERE start_date >= :dtInicio and end_date <= :dtFim", nativeQuery = true)
    public List<Events> fetchEventsInBetweenSqlServer(String dtInicio, String dtFim);

}
