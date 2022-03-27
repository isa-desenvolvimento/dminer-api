package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import org.bouncycastle.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

import com.dminer.entities.Benefits;
import com.dminer.entities.Category;
import com.dminer.entities.Comment;
import com.dminer.entities.Document;
import com.dminer.entities.Events;
import com.dminer.entities.Notice;
import com.dminer.entities.Notification;
import com.dminer.entities.Permission;
import com.dminer.entities.Post;
import com.dminer.entities.Reminder;
import com.dminer.entities.Survey;
import com.dminer.entities.Tutorials;
import com.dminer.entities.User;
import com.dminer.enums.EventsTime;
import com.dminer.enums.PostType;

@Repository
public class GenericRepositorySqlServer {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private UserRepository userRepository;
    
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
        "e.title, ' ', e.content, ' ', e.id, ' ' )) " +
           "LIKE LOWER('%" +keyword+ "%')";

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
    

    public List<Tutorials> searchTutorials(String keyword) {
        String query =
        "SELECT * " +
        "FROM TUTORIALS e " +
        "WHERE LOWER(CONCAT( " +
           " e.category_id, ' ', e.title, ' ', e.permission, ' ', e.content, ' ', e.id, ' ' )) " +
           " LIKE LOWER('%" +keyword+ "%')";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Tutorials e = new Tutorials();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            e.setPermission(rs.getString("PERMISSION"));    
            e.setImage(rs.getString("IMAGE"));

            // Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            // if (p.isPresent())
            //     e.setPermission(p.get());
            
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
    
    
    public List<Notice> searchNotice(String keyword, String login) {
        String query =
        "SELECT * " +
        "FROM NOTICE e " +
        "WHERE LOWER(CONCAT( " +
           "e.creator, ' ', " +
           "e.warning, ' ', " +
           "convert(varchar(100), e.date, 120))) " +
           "LIKE LOWER('%" +keyword+ "%') " +
           "and e.creator = '" +login+ "' " +
           "order by date desc ";

        log.info("searchNotice = {}", query);

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
           "e.type, ' ', " +
           "e.login, ' ', " +
           "convert(varchar(10), e.create_date, 120), ' ', " +
           "e.title, ' ')) " +
           "LIKE LOWER('%" + keyword + "%') " +
           "ORDER BY create_date desc" ;

        log.info("searchPost = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Post e = new Post();
            e.setId(rs.getInt("ID"));
            e.setContent(rs.getString("CONTENT"));
            e.setLogin(rs.getString("LOGIN"));
            e.setTitle(rs.getString("TITLE"));
            PostType type = PostType.valueOf(rs.getString("TYPE"));
            e.setType(type);
            return e;
        });
    }
    
    public List<Comment> searchCommentsByPostIdAndDateAndUser(Post post, String date, Optional<User> user) {
        
        String keyword = "";
        if (date !=  null) keyword += date;
        if (user.isPresent()) keyword += (" " + user.get().getId());
        String query = 
        "SELECT * " +
        "FROM comment e WHERE LOWER(CONCAT( " +
        " e.post_id, ' ', " +
        " convert(varchar(10), e.timestamp, 120), ' ', " +
        " e.user_id, ' ', " +
        " e.content, ' ' )) " +
        "LIKE LOWER('%" + keyword + "%') " +
        "ORDER BY create_date desc" ;
       

        log.info("searchCommentsByPostIdAndDateAndUser = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Comment e = new Comment();
            e.setId(rs.getInt("ID"));
            e.setContent(rs.getString("CONTENT"));
            e.setTimestamp(rs.getTimestamp("TIMESTAMP"));
            e.setPost(post);
            if (user.isPresent()) {
                e.setUser(user.get());
            } else {
                Optional<User> findById = userRepository.findById(rs.getInt("USER_ID"));
                if (findById.isPresent())
                    e.setUser(findById.get());
            }
            return e;
        });
    }

    
    public List<Post> searchPostsByDateOrUser(String date, Optional<User> user) {
        
        String query = "";

        query = 
        "SELECT * " +
        "FROM post p FULL OUTER JOIN comment c on c.post_id = p.id ";
        String[] conditions = new String[]{};

        if (date != null)
            conditions = Arrays.append(
                conditions, " c.timestamp between " + date + " 00:00:00 and " + date + " 23:59:59 " + " OR p.create_date between " + date + " 00:00:00 and " + date + " 23:59:59 "
            );
        if (user.isPresent()) {            
            conditions = Arrays.append(conditions, " c.user_id=" + user.get().getId() + " OR p.login='" + user.get().getLogin() + "'");
        }

        if (!Arrays.isNullOrEmpty(conditions)) {
            query += "WHERE " + String.join(" and ", conditions);            
        }

        log.info("searchPostsByDateOrUser = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Post e = new Post();
            e.setId(rs.getInt("ID"));
            e.setContent(rs.getString("CONTENT"));
            e.setLogin(rs.getString("LOGIN"));
            e.setTitle(rs.getString("TITLE"));
            PostType type = PostType.valueOf(rs.getString("TYPE"));
            e.setType(type);
            return e;
        });
    }


    public List<Document> searchDocuments(String keyword) {
        String query = 
        "SELECT * " +
        "FROM DOCUMENT e " +
        "WHERE LOWER(CONCAT( " +
           " e.content_link, ' ', " +
           " e.title, ' ', " +
           " e.category_id, ' ')) " +           
           " LIKE LOWER('%" + keyword + "%')";
        log.info("searchDocuments = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Document e = new Document();
            e.setId(rs.getInt("ID"));
            e.setContentLink(rs.getString("CONTENT_LINK"));
            e.setTitle(rs.getString("TITLE"));
            e.setPermission(rs.getBoolean("PERMISSION"));
            // Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            // if (p.isPresent())
            //     e.setPermission(p.get());
            Optional<Category> c = categoryRepository.findById(rs.getInt("CATEGORY_ID"));
            if (c.isPresent())
                e.setCategory(c.get());
            return e;
        });
    }


    public List<Notification> searchNotification(String keyword, String login) {
    	String query = 
    			"select " +
    					"   notificati0_.id as id, " +
    					"   notificati0_.active as active, " +
    					"   notificati0_.notification as notification, " +
                        "   notificati0_.create_date as create_date, " +
    					"   notificati0_.user_id as user_id " +
    					"from " +
    					"   notification notificati0_ cross  " +
    					"join " +
    					"   users user1_  " +
    					"where " +
    					"   notificati0_.user_id=user1_.id  " +
    					"   and (user1_.login='" + login + "' or notificati0_.all_users = 1) " ;
                        
    					
    	if (keyword != null) {
    		query += "and ( " ;
    		query += "    lower(notificati0_.notification) like lower('%" + keyword + "%')) ";
    	}
    	query += "   order by notificati0_.create_date desc ";

    	log.info("searchNotification = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Notification e = new Notification();
            e.setId(rs.getInt("ID"));
            e.setActive(rs.getBoolean("ACTIVE"));
            e.setNotification(rs.getString("NOTIFICATION"));
            e.setCreateDate(rs.getTimestamp("CREATE_DATE"));
            Optional<User> findById = userRepository.findById(rs.getInt("USER_ID"));
            if (findById.isPresent())
                e.setUser(findById.get());
            return e;
        });
    }


    public List<Reminder> searchReminder(String keyword, String login) {
        String query = 
        "select * " +
        "from " +
        "   reminder reminder_1 cross  " +
        "join " +
        "   users user1_  " +
        "where " +
        "   reminder_1.user_id=user1_.id  ";
        if (keyword != null) {        	
            query +=
        			"and ( " +        
					"    lower(concat(reminder_1.reminder_describle, ' ', convert(varchar(100), reminder_1.date, 120), ' ', reminder_1.active, ' ', user1_.login, ' ')) like lower('%" + keyword + "%')  " +
					") ";
        }
        query += "    and user1_.login = '" + login + "' " ;
        query += " order by reminder_1.date desc  ";
        
        log.info("searchReminder = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Reminder e = new Reminder();
            e.setId(rs.getInt("ID"));
            e.setActive(rs.getBoolean("ACTIVE"));
            e.setReminderDescrible(rs.getString("REMINDER_DESCRIBLE"));
            e.setDate(rs.getTimestamp("DATE"));
            Optional<User> findById = userRepository.findById(rs.getInt("USER_ID"));
            if (findById.isPresent())
                e.setUser(findById.get());
            return e;
        });
    }


    public List<Notification> getNotificationsByFullCalendarEvents(Integer idUser) {
        String query = 
        "select users_id as user_id, fc.title as notification , fc.start_date as create_date " + 
        "from full_calendar fc " + 
        "inner join full_calendar_users fcu on fcu.full_calendar_id = fc.id " + 
        "WHERE "+ 
            "users_id = " + idUser + " " + 
            "and current_timestamp between start_date and end_date";

        log.info("getNotificationsByFullCalendarEvents = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Notification e = new Notification();            
            e.setNotification(rs.getString("notification"));
            e.setCreateDate(rs.getTimestamp("create_date"));
            e.setUser(new User(rs.getInt("user_id")));
            return e;
        });
    }
}
