package com.bbzbl.task.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Checks if a connection to the database can be established at application startup.
 */
@Component
public class DatabaseChecker {

    private final DataSource dataSource;

    /**
     * Constructor for injecting the data source.
     *
     * @param dataSource the application's configured data source
     */
    public DatabaseChecker(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Bean that runs on application startup to verify the database connection.
     *
     * @return an ApplicationRunner that logs the status of the database connection
     */
    @Bean
    public ApplicationRunner verifyDatabaseConnection() {
        return args -> {
            try (Connection connection = dataSource.getConnection()) {
                System.out.println("✅ Successfully connected to the database.");
            } catch (SQLException exception) {
                System.err.println("❌ Failed to connect to the database: " + exception.getMessage());
            }
        };
    }
}
