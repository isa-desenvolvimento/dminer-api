package com.dminer.dminer.database;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DatabaseTest {
    
    //@Test
    public void testConnection() throws SQLException {

        String connectionUrl =
                "jdbc:sqlserver://localhost:1433;"
                        + "database=master;"
                        + "user=SA;"
                        + "password=<Princes@123>;";

        Connection connection = DriverManager.getConnection(connectionUrl);
        assertNotNull(connection);
    }
}
