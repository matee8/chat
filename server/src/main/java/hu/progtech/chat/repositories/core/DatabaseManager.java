package hu.progtech.chat.repositories.core;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.DatabaseSettings;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseManager {
    private static final String SCHEMA_FILE = "/schema.sql";

    private static DatabaseManager instance;

    private static final Logger logger = LogManager.getLogger(DatabaseManager.class);
    private final DatabaseSettings settings;

    private DatabaseManager(AppConfig config) {
        this.settings = config.database();
        try {
            initDatabase();
            logger.info("Database initialization successful.");
        } catch (Exception e) {
            logger.fatal("Database initialization failed.", e);
            throw new RuntimeException("Could not initialize database.", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                settings.getUrl(), settings.getUsername(), settings.getPassword());
    }

    public static synchronized void initialize(AppConfig config) {
        if (instance == null) {
            if (config == null) {
                throw new IllegalArgumentException(
                        "AppConfig cannot be null during initialization.");
            }
            instance = new DatabaseManager(config);
        } else {
            logger.warn("DatabaseManager singleton already initialized.");
        }
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "DatabaseManager singleton not initialized. Call initialize() first.");
        }

        return instance;
    }

    private void initDatabase() throws SQLException {
        String schemaSql = loadSchemaSql();

        if (schemaSql == null || schemaSql.trim().isEmpty()) {
            logger.warn(
                    "Schema file '{}' is empty or could not be read. "
                            + "Skipping schema initialization.",
                    SCHEMA_FILE);
            return;
        }

        try (Connection connection = getConnection();
                Statement stmt = connection.createStatement()) {
            String[] sqlCommands = schemaSql.split(";\\s*");

            for (String command : sqlCommands) {
                if (command != null && !command.trim().isEmpty()) {
                    logger.debug("Executing schema command: {}", command);
                    stmt.execute(command);
                }

                logger.info("Database schema initialized succesfully.");
            }
        }
    }

    private String loadSchemaSql() {
        try (InputStream is = DatabaseManager.class.getResourceAsStream(SCHEMA_FILE)) {
            if (is == null) {
                logger.error("Schema file '{}' not found in classpath.", SCHEMA_FILE);
                return null;
            }

            byte[] allBytes = is.readAllBytes();
            return new String(allBytes, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.error("Failed to read schema file '{}'", SCHEMA_FILE, ex);
            return null;
        }
    }
}
