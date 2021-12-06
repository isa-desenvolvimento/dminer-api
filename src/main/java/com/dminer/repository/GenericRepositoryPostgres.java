package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import com.dminer.entities.Benefits;
import com.dminer.entities.Category;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Permission;
import com.dminer.entities.Post;
import com.dminer.entities.Survey;
import com.dminer.entities.Tutorials;
import com.dminer.entities.User;
import com.dminer.enums.EventsTime;
import com.dminer.enums.PostType;

@Repository
public class GenericRepositoryPostgres {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private JdbcOperations jdbcSubOperations;

    @Autowired
    private UserRepository userRepository;
    
    //@Autowired
    //private UserService userService;
    
    @Autowired
    private ProfileRepository profileRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    
    private static final Logger log = LoggerFactory.getLogger(GenericRepositoryPostgres.class);

    
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

    public List<Events> searchEvents(String keyword) {
        String query =
        "SELECT * " +
        "FROM EVENTS e " +
        "WHERE lower(CONCAT( " +
           "e.title, ' ', e.location, ' ', e.description, ' ', " +
           "e.start_repeat, ' ', e.end_repeat, ' ', e.reminder, " +
           "to_char(e.start_date, 'yyyy-mm-dd hh:mm:ss'), ' ', " +
           "to_char(e.end_date , 'yyyy-mm-dd hh:mm:ss'))) " +
           " LIKE LOWER('%" +keyword+ "%')";

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


    private String token = null;


    public List<Benefits> searchBenefits(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE lower(CONCAT( " +
           "e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'), ' ', " +
           " LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Benefits e = new Benefits();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            Optional<User> findById = userRepository.findById(rs.getInt("CREATOR_ID"));
            if (findById.isPresent())
                e.setCreator(findById.get());
            
            Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            if (p.isPresent())
                e.setPermission(p.get());            
            e.setImage(rs.getString("IMAGE"));
            return e;
        });
    }


    public List<Tutorials> searchTutorial(String keyword) {
        String query =
        "SELECT * " +
        "FROM TUTORIALS e " +
        "WHERE lower(CONCAT( " +
           "e.profile, ' ', e.category, ' ', e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'), ' ', " +
           " LIKE LOWER('%" +keyword+ "%')";

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
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'), ' ')) " +
           " LIKE LOWER('%" +keyword+ "%')";

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
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'), ' ')) " +
           " LIKE LOWER('%" +keyword+ "%')";

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
    
    
    public List<Post> searchPost(String keyword) {
        String query = 
        "SELECT * " +
        "FROM POST e " +
        "WHERE LOWER(CONCAT( " +
           "e.content, ' ', " +
           "e.likes, ' ', " +
           "e.type, ' ', " +
           "e.login, ' ', " +
           "e.title, ' '))" +
           " LIKE LOWER('%" + keyword + "%')";
        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Post e = new Post();
            e.setId(rs.getInt("ID"));
            e.setContent(rs.getString("CONTENT"));
            e.setLikes(rs.getInt("LIKES"));
            e.setLogin(rs.getString("LOGIN"));
            e.setTitle(rs.getString("TITLE"));
            PostType type = PostType.valueOf(rs.getString("TYPE"));
            e.setType(type);
            return e;
        });
    }
}    