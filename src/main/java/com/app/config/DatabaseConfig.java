package com.app.config;

import com.app.exception.custom.DatabaseConnectionException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

import static com.app.util.MessageConstants.DATABASE_CONNECTION_ERROR;
import static com.app.util.Utils.tagMethodName;


@Configuration
public class DatabaseConfig {

    private final LoggerService logger;
    private static final String TAG = "DatabaseConfig";

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Autowired
    public DatabaseConfig(LoggerService logger) {
        this.logger = logger;
    }

    @Bean
    public DataSource dataSource() {
        String methodName = "dataSource";
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        HikariDataSource dataSource = new HikariDataSource(config);
        // Try to get a connection at startup to ensure DB is reachable
        try (Connection connection = dataSource.getConnection()) {
            logger.info(tagMethodName(TAG, methodName), "Database connected successfully: " + connection.getMetaData().getURL());
        } catch (Exception e) {
            logger.info(tagMethodName(TAG, methodName), "Failed to connect to the database at startup: " + e.getMessage());
            throw new DatabaseConnectionException(DATABASE_CONNECTION_ERROR);
        }
        return dataSource;
    }
}
