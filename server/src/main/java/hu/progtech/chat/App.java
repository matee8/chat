package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.event.ChatEventBus;
import hu.progtech.chat.event.InMemoryChatEventBus;
import hu.progtech.chat.model.Message;
import hu.progtech.chat.model.User;
import hu.progtech.chat.repository.H2MessageRepository;
import hu.progtech.chat.repository.H2UserRepository;
import hu.progtech.chat.repository.MessageRepository;
import hu.progtech.chat.repository.UserRepository;
import hu.progtech.chat.service.AuthService;
import hu.progtech.chat.service.AuthServiceImpl;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.ChatServiceImpl;
import hu.progtech.chat.util.DatabaseManager;
import hu.progtech.chat.util.TokenManager;

public class App {
    public static void main(String[] args) {
        try {
            AppConfig config = AppConfigLoader.loadConfig();

            ChatEventBus eventBus = new InMemoryChatEventBus();

            DatabaseManager databaseManager = new DatabaseManager(config.databaseSettings());

            UserRepository userRepository = new H2UserRepository(databaseManager);

            MessageRepository messageRepository = new H2MessageRepository(databaseManager);

            TokenManager tokenManager = new TokenManager(config.tokenSettings());

            AuthService authService = new AuthServiceImpl(userRepository, tokenManager);

            ChatService chatService =
                    new ChatServiceImpl(messageRepository, userRepository, eventBus, tokenManager);

            User user = authService.register("mate", "password");

            String token = authService.login(user.username(), "password");

            chatService.sendMessage(token, "Hi!");

            for (Message message : chatService.getChatHistory()) {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
