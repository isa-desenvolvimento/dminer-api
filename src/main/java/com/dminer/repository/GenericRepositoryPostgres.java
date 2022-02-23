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
public class GenericRepositoryPostgres {
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;


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

    public List<Benefits> searchBenefits(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE lower(CONCAT( " +
           "e.title, ' ', e.content, ' ', " +
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'), ' ')) " +
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
           "e.profile, ' ', e.category, ' ', e.title, ' ', e.content, ' ', " +
           "to_char(e.date, 'yyyy-mm-dd hh:mm:ss'))) " +
           " LIKE LOWER('%" +keyword+ "%') ORDER BY e.date DESC;";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Tutorials e = new Tutorials();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            // Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            // if (p.isPresent())
                // e.setPermission(p.get());
            e.setPermission(rs.getString("PERMISSION"));    
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
           "LIKE LOWER('%" +keyword+ "%') " +
           "order by date desc ";

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
           "e.type, ' ', " +
           "e.login, ' ', " +
           "e.title, ' ')) " +
           "LIKE LOWER('%" + keyword + "%') " + 
           "ORDER BY create_date desc" ;
        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Post e = new Post();
            e.setId(rs.getInt("ID"));
            e.setContent(rs.getString("CONTENT"));
            e.setLogin(rs.getString("LOGIN"));
            e.setTitle(rs.getString("TITLE"));
            e.setCreateDate(rs.getTimestamp("CREATE_DATE"));
            PostType type = PostType.valueOf(rs.getString("TYPE"));
            e.setType(type);
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
    					"   and user1_.login='" + login + "' or notificati0_.all_users is true" ;
    					
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
					"    lower(concat(reminder_1.reminder_describle, ' ', to_char(reminder_1.date, 'yyyy-mm-dd hh:mm:ss'), ' ', reminder_1.active, ' ', user1_.login, ' ')) like lower('%" + keyword + "%')  " +
					") ";
        }
        query += "    and user1_.login = '" + login + "' " ;
        query += " order by reminder_1.date desc  ";
        
        log.info("search = {}", query);

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
    
    
    
    public List<Document> searchDocuments(String keyword) {
        String query = 
        "SELECT * " +
        "FROM DOCUMENT e " +
        "WHERE LOWER(CONCAT( " +
           "e.content_link, ' ', " +
           "e.title, ' ', " +
           "e.category_id, ' ')) " +           
           " LIKE LOWER('%" + keyword + "%')";
        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
        	Document e = new Document();
            e.setId(rs.getInt("ID"));
            e.setContentLink(rs.getString("CONTENT_LINK"));
            e.setTitle(rs.getString("TITLE"));
            e.setCreateDate(rs.getTimestamp("CREATE_DATE"));
            // Optional<Permission> p = permissionRepository.findById(rs.getInt("PERMISSION_ID"));
            // if (p.isPresent())
            //     e.setPermission(p.get());
            e.setPermission(rs.getBoolean("PERMISSION"));
            Optional<Category> c = categoryRepository.findById(rs.getInt("CATEGORY_ID"));
            if (c.isPresent())
                e.setCategory(c.get());
            return e;
        });
    }
    

    public List<Comment> searchCommentsByPostIdAndDateAndUser(Post post, String date, Optional<User> user) {
        String query = 
        "SELECT * " +
        "FROM comment e WHERE ";
        String[] conditions = new String[]{};

        conditions = Arrays.append(conditions, " e.post_id=" + post.getId());
        if (date != null)
            conditions = Arrays.append(conditions, " e.timestamp='" + date + "'");
        if (user.isPresent())
            conditions = Arrays.append(conditions, " e.user_id=" + user.get().getId());

        if (!Arrays.isNullOrEmpty(conditions)) {
            query += String.join(" and ", conditions);
        }

        log.info("search = {}", query);

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
        
        String query = 
        "SELECT * " +
        "FROM post p FULL OUTER JOIN comment c on c.post_id = p.id ";
        String[] conditions = new String[]{};

        if (date != null)
            conditions = Arrays.append(conditions, " c.timestamp='" + date + "' OR p.create_date='" + date + "'");
        if (user.isPresent()) {            
            conditions = Arrays.append(conditions, " c.user_id=" + user.get().getId() + " OR p.login='" + user.get().getLogin() + "'");
        }

        if (!Arrays.isNullOrEmpty(conditions)) {
            query += "WHERE " + String.join(" and ", conditions);            
        }

        log.info("search = {}", query);

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




    public List<Notification> getNotificationsByFullCalendarEvents(Integer idUser) {
        String query = 
        "select users_id as user_id, fc.title as notification , fc.start_date as create_date, u.login as login " + 
        "from full_calendar fc " + 
        "inner join full_calendar_users fcu on fcu.full_calendar_id = fc.id " +
        "inner join users u on u.id = fcu.users_id " + 
        "WHERE "+ 
            "users_id = " + idUser + " " + 
            "and current_timestamp between start_date and end_date";

        log.info("getNotificationsByFullCalendarEvents = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Notification e = new Notification();            
            e.setNotification(rs.getString("notification"));
            e.setCreateDate(rs.getTimestamp("create_date"));
            e.setUser(new User(rs.getInt("user_id"), rs.getString("login")));
            return e;
        });
    }

    
}    