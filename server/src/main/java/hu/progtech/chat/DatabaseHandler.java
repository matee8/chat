package hu.progtech.chat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum DatabaseHandler {
    INSTANCE;

    private final Logger logger = LogManager.getLogger(DatabaseHandler.class);
    private static final String DB_URL = "jdbc:h2:./chatdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static final String SCHEMA_FILE = "/schema.sql";

    private final Connection connection;

    private DatabaseHandler() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initDatabase();
            logger.info("Database connection established to {}", DB_URL);
        } catch (Exception ex) {
            logger.error("Database initialization failed", ex);
            throw new RuntimeException("Could not initialize database", ex);
        }
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

        try (Statement stmt = connection.createStatement()) {
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
        try (InputStream is = DatabaseHandler.class.getResourceAsStream(SCHEMA_FILE)) {
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

    public Connection getConnection() {
        return connection;
    }
}
