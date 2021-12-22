package com.dminer.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Repository;

@Repository
public class SurveyResponseUsersRepository {
    
    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyResponseRepository surveyResponseRepository;

    @Autowired
    private JdbcOperations jdbcOperations;


    public void persist(Integer idSurveyResponse, Integer idUser) {
        String query = "SELECT COUNT(*) AS QTD " + 
        "FROM survey_responses_users WHERE survey_responses_id=" + idSurveyResponse + " AND users_id=" + idUser;

        System.out.println(query);
        boolean jahExiste = jdbcOperations.query(query, (rs) -> {
            if (rs.next()) {
                return rs.getInt("QTD") == 0 ? false : true;
            }
            return false;
        });

        if (!jahExiste) {
            query = "INSERT INTO survey_responses_users " + 
            "(survey_responses_id, users_id) VALUES(?, ?)";

            System.out.println(query);
            jdbcOperations.update(query, idSurveyResponse, idUser);
        }
    }


}
