package com.dminer.repository;

import java.util.List;
import com.dminer.entities.Tutorials;
import com.dminer.enums.Category;
import com.dminer.enums.Profiles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class TutorialsRepositorySqlServer {
    
    private static final Logger log = LoggerFactory.getLogger(TutorialsRepositorySqlServer.class);
    
    @Autowired
    private JdbcOperations jdbcOperations;


    public List<Tutorials> search(String keyword) {
        String query =
        "SELECT * " +
        "FROM BENEFITS e " +
        "WHERE CONCAT( " +
           "e.profile, ' ', e.category, ' ', e.title, ' ', e.location, ' ', e.content, ' ', e.profiles, ' ', " +
           "' ', convert(varchar(100), e.date, 120)) " +
           "LIKE '%" +keyword+ "%'";

        log.info("search = {}", query);

        return jdbcOperations.query(query, (rs, rowNum) -> { 
            Tutorials e = new Tutorials();
            e.setId(rs.getInt("ID"));
            e.setTitle(rs.getString("TITLE"));
            e.setContent(rs.getString("CONTENT"));
            e.setDate(rs.getTimestamp("DATE"));
            e.setCategory(Category.valueOf(rs.getString("CATEGORY")));
            e.setProfile(
                rs.getString("PROFILES") != null ? Profiles.valueOf(rs.getString("PROFILES")) : null
            );
            e.setImage(rs.getString("IMAGE"));
            return e;
        });

    }

}
