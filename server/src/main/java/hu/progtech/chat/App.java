package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.models.User;
import hu.progtech.chat.repositories.H2UserRepository;
import hu.progtech.chat.repositories.UserRepository;
import hu.progtech.chat.repositories.core.DatabaseManager;
import hu.progtech.chat.services.AuthService;
import hu.progtech.chat.services.AuthServiceImpl;
import hu.progtech.chat.util.TokenManager;

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();
        DatabaseManager databaseManager = new DatabaseManager(config.databaseSettings());

        UserRepository userRepository;
        try {
            userRepository = new H2UserRepository(databaseManager);

            TokenManager tokenManager = new TokenManager(config.tokenSettings());

            AuthService authService = new AuthServiceImpl(userRepository, tokenManager);

            User user = authService.register("mate", "password");

            authService.login(user.username(), "password");
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
