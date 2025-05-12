package hu.progtech.chat.repositories;

import hu.progtech.chat.DatabaseHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mindrot.jbcrypt.BCrypt;

public class UserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public void saveUser(String username, String password) throws SQLException {
        if (username == null
                || username.trim().isEmpty()
                || password == null
                || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Username and password cannot be empty.");
        }

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = DatabaseHandler.INSTANCE.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            logger.info("User '{}' saved succesfully.", username);
        } catch (SQLException ex) {
            logger.error("Error registering user: {}", username, ex);
            throw ex;
        }
    }

    public boolean validateUser(String username, String password) throws SQLException {
        String sql = "SELECT password FROM users WHERE username = ?";

        try (Connection conn = DatabaseHandler.INSTANCE.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            String passwordHash;

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    passwordHash = rs.getString("password");
                } else {
                    return false;
                }
            }

            logger.info("User '{}' authenticated succesfully.", username);
            return BCrypt.checkpw(password, passwordHash);
        } catch (SQLException ex) {
            logger.error("Error authenticating user: {}", username, ex);
            throw ex;
        }
    }
}
