package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Events;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventsTimeRepository extends JpaRepository<Events, Integer> {
    
    // postgres -> prod heroku
    @Query("FROM Events e WHERE EXTRACT(YEAR FROM e.startDate) = :year or EXTRACT(YEAR FROM e.endDate) = :year")
    public List<Events> fetchEventsByYear(@Param("year") Integer year);

    @Query("FROM Events e WHERE EXTRACT(MONTH FROM e.endDate) = :month or EXTRACT(MONTH FROM e.startDate) = :month " +
    "and EXTRACT(YEAR FROM e.endDate) = :year and EXTRACT(MONTH FROM e.startDate) = :year")
    public List<Events> fetchEventsByMonth(@Param("year") Integer year, @Param("month") Integer month);

    // @Query(value = "FROM Events e WHERE e.endDate = :date or e.startDate = :date")
    // public List<Events> fetchEventsByDate(@Param("date") Timestamp date);

    // @Query(value = "SELECT * FROM Events WHERE start_date >= :dtInicio and end_date <= :dtFim", nativeQuery = true)
    // public List<Events> fetchEventsInBetween(String dtInicio, String dtFim);


    //sql server -> dev
    @Query(value= "SELECT * FROM Events WHERE year(end_date) = :year or year(start_date) = :year", nativeQuery = true)
    public List<Events> fetchEventsByYearSqlServer(@Param("year") String year);

    @Query(value = "SELECT * FROM Events WHERE month(end_date) = :month or month(start_date) = :month " +
    "and year(end_date) = :year and year(start_date) = :year", nativeQuery = true)
    public List<Events> fetchEventsByMonthSqlServer(@Param("year") String year, @Param("month") String month);

    @Query(value = "SELECT * FROM Events WHERE end_date = :date or start_date = :date", nativeQuery = true)
    public List<Events> fetchEventsByDateSqlServer(@Param("date") String date);
    
    @Query(value = "SELECT * FROM Events WHERE start_date >= :dtInicio and end_date <= :dtFim", nativeQuery = true)
    public List<Events> fetchEventsInBetweenSqlServer(String dtInicio, String dtFim);

}
