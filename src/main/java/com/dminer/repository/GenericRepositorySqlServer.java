package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import com.dminer.converters.NoticeConverter;
import com.dminer.dto.UserDTO;
import com.dminer.entities.Benefits;
import com.dminer.entities.Category;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Permission;
import com.dminer.entities.Survey;
import com.dminer.entities.Tutorials;
import com.dminer.entities.User;
import com.dminer.enums.EventsTime;
import com.dminer.services.UserService;
import com.dminer.utils.UtilDataHora;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class GenericRepositorySqlServer {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private UserRepository userService;
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    private static final Logger log = LoggerFactory.getLogger(GenericRepositorySqlServer.class);


    public List<Events> fetchEventsByYear (String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE "+
            "E.START_DATE >= CONVERT(VARCHAR, '"+data1+"', 20) AND " +
            "E.END_DATE <= CONVERT(VARCHAR, '"+data2+"', 20) ";
        
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
            "E.START_DATE >= CONVERT(VARCHAR, '"+data1+"', 20) AND E.START_DATE <= CONVERT(VARCHAR, '"+data2+"', 20) OR " +
            "E.END_DATE >= CONVERT(VARCHAR, '"+data1+"', 20) AND E.END_DATE <= CONVERT(VARCHAR, '"+data2+"', 20)";

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


    public List<Events> fetchEventsByDate (String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE "+
            "E.START_DATE BETWEEN CONVERT(VARCHAR, '"+data1+"', 20) AND CONVERT(VARCHAR, '"+data2+"', 20)";

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
    }

    
    public List<Events> fetchEventsInBetween(String data1, String data2) {
        String query = 
        "SELECT * " + 
        "FROM EVENTS E "+
        "WHERE "+
            "E.START_DATE >= CONVERT(VARCHAR, '"+data1+"', 20) AND E.START_DATE <= CONVERT(VARCHAR, '"+data2+"', 20)";

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



    public List<Events> searchEvents(String keyword) {
        String query =
        "SELECT * " +
        "FROM EVENTS e " +
        "WHERE LOWER(CONCAT( " +
           "e.title, ' ', e.location, ' ', e.description, ' ', " +
           "e.start_repeat, ' ', e.end_repeat, ' ', e.reminder, " +
           "' ', convert(varchar(100), e.start_date, 120), " +
           "' ', convert(varchar(100), e.end_date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%')";

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


    public List<Benefits> searchBenefits(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE LOWER(CONCAT( " +
           "e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "' ', convert(varchar(100), e.date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Benefits e = new Benefits();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            Optional<User> findById = userService.findById(rs.getInt("CREATOR"));
            if (findById.isPresent())
                e.setCreator(findById.get());
            
            Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            if (p.isPresent())
                e.setPermission(p.get());            
            e.setImage(rs.getString("IMAGE"));
            return e;
        });

    }
    

    public List<Tutorials> searchTutorials(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE LOWER(CONCAT( " +
           "e.profile, ' ', e.category, ' ', e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "' ', convert(varchar(100), e.date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Tutorials e = new Tutorials();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            if (p.isPresent())
                e.setPermission(p.get());
            Optional<Category> c = categoryRepository.findById(rs.getInt("CATEGORY_ID"));
            if (c.isPresent())
                e.setCategory(c.get());            
            e.setImage(rs.getString("IMAGE"));
            return e;
        });
    }


    public List<Survey> searchSurvey(String keyword) {
        String query =
        "SELECT * " +
        "FROM SURVEY e " +
        "WHERE LOWER(CONCAT( " +
           "e.question, ' ', " +
           "convert(varchar(100), e.date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Survey e = new Survey();
            e.setId(rs.getInt("ID"));
            e.setOptionA(rs.getString("OPTIONA"));
            e.setOptionB(rs.getString("OPTIONB"));
            e.setQuestion(rs.getString("QUESTION"));
            e.setDate(rs.getTimestamp("DATE"));
            return e;
        });
    }
    
    
    public List<Notice> searchNotice(String keyword) {
        String query =
        "SELECT * " +
        "FROM NOTICE e " +
        "WHERE LOWER(CONCAT( " +
           "e.creator, ' ', " +
           "e.warning, ' ', " +
           "convert(varchar(100), e.date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Notice e = new Notice();
            e.setId(rs.getInt("ID"));
            e.setCreator(rs.getString("CREATOR"));
            e.setWarning(rs.getString("WARNING"));
            e.setPriority(rs.getInt("PRIORITY"));
            e.setDate(rs.getTimestamp("DATE"));
            return e;
        });
    }

}
