package com.dminer.repository;

import java.util.List;
import java.util.Optional;

import com.dminer.entities.Benefits;
import com.dminer.entities.Reminder;
import com.dminer.entities.User;
import com.dminer.enums.Profiles;
import com.dminer.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class BenefitsRepositorySqlServer {
    
    private static final Logger log = LoggerFactory.getLogger(BenefitsRepositorySqlServer.class);
    
    @Autowired
    private JdbcOperations jdbcOperations;

    @Autowired
    private UserService userService;
    


    public List<Benefits> search(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE CONCAT( " +
           "e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "' ', convert(varchar(100), e.date, 120)) " +
           "LIKE '%" +keyword+ "%'";

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
            
            e.setProfiles(
                rs.getString("PROFILES") != null ? Profiles.valueOf(rs.getString("PROFILES")) : null
            );

            e.setImage(rs.getBytes("IMAGE"));
            return e;
        });

    }

}