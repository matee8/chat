package hu.progtech.chat.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserSessionService {
    private static final Logger LOGGER = LogManager.getLogger(UserSessionService.class);

    private String token;
    private String username;
    private boolean isAuthenticated;

    public UserSessionService() {
        this.token = null;
        this.username = null;
        this.isAuthenticated = false;
    }

    public void login(final String token, final String username) {
        if (token == null || token.isBlank()) {
            LOGGER.warn("Login attempt with invalid token.");
            return;
        }

        if (username == null || username.isBlank()) {
            LOGGER.warn("Login attempt with invalid username.");
            return;
        }

        this.token = token;
        this.username = username;
        this.isAuthenticated = true;

        LOGGER.info("User '{}' logged in successfully.", username);
    }

    public void logout() {
        this.token = null;
        this.username = null;
        this.isAuthenticated = false;

        LOGGER.info("User '{}' logged out successfully.", username);
    }

    public String token() {
        return token;
    }

    public String username() {
        return username;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }
}
