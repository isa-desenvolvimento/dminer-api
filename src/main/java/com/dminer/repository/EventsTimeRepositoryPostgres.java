package com.dminer.repository;

import java.util.List;

import com.dminer.dto.UserDTO;
import com.dminer.entities.Events;
import com.dminer.enums.EventsTime;
import com.dminer.utils.UtilDataHora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class EventsTimeRepositoryPostgres {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    private static final Logger log = LoggerFactory.getLogger(EventsTimeRepositoryPostgres.class);

    
    public List<Events> fetchEventsByYear (String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE " +
            "E.START_DATE >= TO_TIMESTAMP('"+data1+"', 'YYYY-MM-DD HH:MI:SS') AND E.START_DATE <= TO_TIMESTAMP('"+data2+"', 'YYYY-MM-DD HH:MI:SS') OR " +
            "E.END_DATE >= TO_TIMESTAMP('"+data1+"', 'YYYY-MM-DD HH:MI:SS') AND E.END_DATE <= TO_TIMESTAMP('"+data2+"', 'YYYY-MM-DD HH:MI:SS')";
        
        log.info("fetchEventsByYear = {}", query);

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
    }
    

    public List<Events> fetchEventsByMonth (String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE "+
            "E.START_DATE >= TO_TIMESTAMP('"+data1+"', 'YYYY-MM-DD HH:MI:SS') AND E.START_DATE <= TO_TIMESTAMP('"+data2+"', 'YYYY-MM-DD HH:MI:SS') OR " +
            "E.END_DATE >= TO_TIMESTAMP('"+data1+"', 'YYYY-MM-DD HH:MI:SS') AND E.END_DATE <= TO_TIMESTAMP('"+data2+"', 'YYYY-MM-DD HH:MI:SS')";

        log.info("fetchEventsByMonth = {}", query);

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
    }


    public List<Events> fetchEventsByDate(String data1, String data2) {
        String query = 
        "SELECT * " +
        "FROM EVENTS EV " + 
        "WHERE " +
            "EV.START_DATE BETWEEN TO_TIMESTAMP('" + data1 + "', 'YYYY-MM-DD HH:MI:SS') " +
            "AND TO_TIMESTAMP('" + data2 + "', 'YYYY-MM-DD HH:MI:SS');";

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


    public List<Events> fetchEventsInBetween(String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE "+
            "E.START_DATE >= TO_TIMESTAMP('"+data1+"', 'YYYY-MM-DD HH:MI:SS') AND E.START_DATE <= TO_TIMESTAMP('"+data2+"', 'YYYY-MM-DD HH:MI:SS')";
        
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
    }

    public List<Events> search(String keyword) {
        String query =
        "SELECT * " +
        "FROM EVENTS e " +
        "WHERE lower(CONCAT( " +
           "e.title, ' ', e.location, ' ', e.description, ' ', " +
           "e.start_repeat, ' ', e.end_repeat, ' ', e.reminder, " +
           "to_char(e.start_date, 'yyyy-mm-dd hh:mm:ss'), ' ', " +
           "to_char(e.end_date , 'yyyy-mm-dd hh:mm:ss'))) " +
           "LIKE lower('%" +keyword+ "%')";

        log.info("search = {}", query);

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

    }


    public List<UserDTO> getBirthDaysOfMonth() {

        String query = 
        "SELECT * " + 
        "FROM USERS U " +
        "WHERE " +
        "   EXTRACT( " +
        "       month from u.dt_birthday" +
        ") = EXTRACT ( " +
        "        MONTH FROM TIMESTAMP '" + UtilDataHora.currentFirstDayFormat() +
        ")";
        
        

        log.info("getBirthDaysOfMonth = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> {
            UserDTO u = new UserDTO();
            
            u.setId(rs.getInt("ID"));
            u.setName(rs.getString("NAME"));
            u.setDtBirthday(rs.getString("DT_BIRTHDAY"));
            u.setAvatar(rs.getBytes("AVATAR_ID"));
            return u;
        });
    }

}
