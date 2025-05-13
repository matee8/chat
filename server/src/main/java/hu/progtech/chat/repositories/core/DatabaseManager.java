package hu.progtech.chat.repositories.core;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.DatabaseSettings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private static final String SCHEMA_FILE = "/schema.sql";

    private static DatabaseManager instance;

    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private final DatabaseSettings settings;

    private DatabaseManager(AppConfig config) {
        this.settings = config.database();
        logger.info("Database initialization successful.");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                settings.getUrl(), settings.getUsername(), settings.getPassword());
    }

    public static synchronized void initialize(AppConfig config) throws IllegalArgumentException {
        if (instance == null) {
            if (config == null) {
                logger.error("AppConfig is null during initialization.");
                throw new IllegalArgumentException(
                        "AppConfig cannot be null during initialization.");
            }
            instance = new DatabaseManager(config);
        } else {
            logger.warn("DatabaseManager singleton already initialized.");
        }
    }

    public static DatabaseManager getInstance() throws IllegalStateException {
        if (instance == null) {
            logger.error("DatabaseManager singleton not initialized when calling getInstance().");
            throw new IllegalStateException(
                    "DatabaseManager singleton not initialized. Call initialize() first.");
        }

        return instance;
    }
}
