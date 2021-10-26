package com.dminer.repository;

import java.util.List;

import com.dminer.entities.Events;
import com.dminer.enums.EventsTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class EventsTimeRepository2 {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    private static final Logger log = LoggerFactory.getLogger(EventsTimeRepository2.class);

    
    public List<Events> fetchEventsByDate(String date) {
        String query = "select * from events ev where " +
        "ev.end_date=to_timestamp('" + date + "', 'YYYY-MM-DD HH:MI:SS') " +
        "or ev.start_date=to_timestamp('" + date + "', 'YYYY-MM-DD HH:MI:SS');";
        
        log.info("fetchEventsByDate = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Events e = new Events();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setStartDate(rs.getTimestamp("START_DATE"));
            e.setEndDate(rs.getTimestamp("END_DATE"));
            e.setAllDay(rs.getBoolean("ALL_DAY"));
            e.setStartRepeat(
                rs.getString("START_REPEAT") != null ? EventsTime.valueOf(rs.getString("START_REPEAT")) : null
            );
            e.setEndRepeat(
                rs.getString("END_REPEAT") != null ? EventsTime.valueOf(rs.getString("END_REPEAT")) : null
            );
            e.setLocation(rs.getString("LOCATION"));
            e.setReminder(
                rs.getString("REMINDER") != null ? EventsTime.valueOf(rs.getString("REMINDER")) : null
            );
            return e;
        });

    };


    public List<Events> fetchEventsInBetween(String dtInicio, String dtFim) {
        String query = "select * from events ev where " +
        "ev.end_date BETWEEN to_timestamp('" + dtInicio + "', 'YYYY-MM-DD HH:MI:SS') and to_timestamp('" + dtFim + "', 'YYYY-MM-DD HH:MI:SS') " +
        "or ev.start_date BETWEEN to_timestamp('" + dtInicio + "', 'YYYY-MM-DD HH:MI:SS') and to_timestamp('" + dtFim + "', 'YYYY-MM-DD HH:MI:SS');";
        
        log.info("fetchEventsInBetween = {}", query);
        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Events e = new Events();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setStartDate(rs.getTimestamp("START_DATE"));
            e.setEndDate(rs.getTimestamp("END_DATE"));
            e.setAllDay(rs.getBoolean("ALL_DAY"));
            e.setStartRepeat(
                rs.getString("START_REPEAT") != null ? EventsTime.valueOf(rs.getString("START_REPEAT")) : null
            );
            e.setEndRepeat(
                rs.getString("END_REPEAT") != null ? EventsTime.valueOf(rs.getString("END_REPEAT")) : null
            );
            e.setLocation(rs.getString("LOCATION"));
            e.setReminder(
                rs.getString("REMINDER") != null ? EventsTime.valueOf(rs.getString("REMINDER")) : null
            );
            return e;
        });

    };


}
