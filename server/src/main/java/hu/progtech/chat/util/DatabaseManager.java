package hu.progtech.chat.util;

import hu.progtech.chat.config.DatabaseSettings;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private static final Logger LOGGER = LogManager.getLogger(DatabaseManager.class);
    private final DatabaseSettings settings;

    public DatabaseManager(final DatabaseSettings settings) {
        if (settings == null) {
            LOGGER.error("DatabaseSettings cannot be null during DatabaseManager instantiation.");
            throw new IllegalArgumentException("DatabaseSettings cannot be null.");
        }
        this.settings = settings;
        LOGGER.info("DatabaseManager initialized with URL: {}", settings.url());
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                settings.url(), settings.username(), settings.password());
    }
}
