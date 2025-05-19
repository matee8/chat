package hu.progtech.chat.repository;

import hu.progtech.chat.config.DatabaseSettings;
import hu.progtech.chat.util.DatabaseManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

abstract class H2BaseRepositoryTest {
    protected static final String H2_URL = "jdbc:h2:./chat_test_db";
    protected static final String H2_DB_USER = "sa";
    protected static final String H2_DB_PASSWORD = "";

    protected DatabaseManager databaseManager;

    @BeforeEach
    void setUpH2Base() throws SQLException {
        databaseManager =
                new DatabaseManager(new DatabaseSettings(H2_URL, H2_DB_USER, H2_DB_PASSWORD));
    }

    @AfterEach
    void cleanUpH2Base() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
                Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS messages");
            stmt.execute("DROP TABLE IF EXISTS users");
        }
    }
}
