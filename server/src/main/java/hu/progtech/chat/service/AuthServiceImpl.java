package hu.progtech.chat.service;

import hu.progtech.chat.model.User;
import hu.progtech.chat.repository.RepositoryException;
import hu.progtech.chat.repository.UserRepository;
import hu.progtech.chat.util.PasswordHasher;
import hu.progtech.chat.util.TokenManager;
import hu.progtech.chat.util.TokenValidationException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = LogManager.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenManager tokenManager;

    public AuthServiceImpl(final UserRepository userRepository, final TokenManager tokenManager) {
        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }

        if (tokenManager == null) {
            throw new IllegalArgumentException("TokenManager cannot be null.");
        }

        this.userRepository = userRepository;
        this.tokenManager = tokenManager;
    }

    @Override
    public User register(final String username, final String password) throws ServiceException {
        if (username == null || username.isBlank()) {
            throw new ServiceException("Username cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new ServiceException("Password cannot be empty.");
        }

        if (password.length() < 8) {
            throw new ServiceException("Password must be at least 8 characters long.");
        }

        try {
            LOGGER.info("Attempting to register user: {}.", username);

            final Optional<User> existingUser = userRepository.findByUsername(username);
            if (existingUser.isPresent()) {
                LOGGER.warn("Registration failed for user {}: username already exists.", username);
                throw new ServiceException("Username '" + username + "' already exists.");
            }

            final String passwordHash = PasswordHasher.hashPassword(password);
            final User newUser = new User(username, passwordHash);
            final User savedUser = userRepository.save(newUser);

            return savedUser;
        } catch (RepositoryException e) {
            LOGGER.error(
                    "Repository error during registration for user {}: {}.",
                    username,
                    e.getMessage(),
                    e);
            if (e.getMessage() != null && e.getMessage().contains("already exists")) {
                LOGGER.warn("Registration failed for user {}: username already exists.", username);
                throw new ServiceException("Username '" + username + "' already exists.");
            }
            throw new ServiceException("Registration failed due to a system error.", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                    "Invalid argument during registration for user {}: {}.",
                    username,
                    e.getMessage(),
                    e);
            throw new ServiceException("Registration failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String login(final String username, final String password) throws ServiceException {
        if (username == null || username.isBlank()) {
            throw new ServiceException("Username cannot be empty.");
        }

        if (password == null || password.isBlank()) {
            throw new ServiceException("Password cannot be empty.");
        }

        try {
            LOGGER.info("Attempting to login user: {}.", username);
            final Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                LOGGER.warn("Login failed for user {}: user not found.", username);
                throw new ServiceException("Invalid username or password.");
            }

            final User user = userOptional.get();
            if (!PasswordHasher.checkPassword(password, user.passwordHash())) {
                LOGGER.warn("Login failed for user {}: Incorrect password.", username);
                throw new ServiceException("Invalid username or password.");
            }

            final String token = tokenManager.generateToken(user);
            LOGGER.info("User {} logged in successfully. Token generated.", username);

            return token;
        } catch (IllegalArgumentException | TokenValidationException e) {
            LOGGER.error("Error generating token for user {}: {}.", username, e.getMessage(), e);
            throw new ServiceException("Login failed: could not generate authentication token.", e);
        }
    }
}
