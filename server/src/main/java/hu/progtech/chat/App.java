package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.models.Message;
import hu.progtech.chat.models.User;
import hu.progtech.chat.repositories.H2MessageRepository;
import hu.progtech.chat.repositories.H2UserRepository;
import hu.progtech.chat.repositories.MessageRepository;
import hu.progtech.chat.repositories.UserRepository;
import hu.progtech.chat.repositories.core.DatabaseManager;
import hu.progtech.chat.services.AuthService;
import hu.progtech.chat.services.AuthServiceImpl;
import hu.progtech.chat.services.ChatService;
import hu.progtech.chat.services.ChatServiceImpl;
import hu.progtech.chat.util.TokenManager;

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();
        DatabaseManager databaseManager = new DatabaseManager(config.databaseSettings());

        try {
            UserRepository userRepository = new H2UserRepository(databaseManager);

            TokenManager tokenManager = new TokenManager(config.tokenSettings());

            AuthService authService = new AuthServiceImpl(userRepository, tokenManager);

            User user = authService.register("mate", "password");

            String token = authService.login(user.username(), "password");

            MessageRepository messageRepository = new H2MessageRepository(databaseManager);

            ChatService chatService =
                    new ChatServiceImpl(messageRepository, userRepository);

            chatService.sendMessage(user.id(), "Hi!");

            for (Message message : chatService.getChatHistory()) {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
