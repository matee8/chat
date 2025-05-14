package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.events.ChatEventBus;
import hu.progtech.chat.events.InMemoryChatEventBus;
import hu.progtech.chat.models.Message;
import hu.progtech.chat.models.User;
import hu.progtech.chat.networking.ClientSubscriptionManager;
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
        try {
            AppConfig config = AppConfigLoader.loadConfig();

            ChatEventBus eventBus = new InMemoryChatEventBus();

            eventBus.subscribe(
                    ClientSubscriptionManager.GLOBAL_CHAT_TOPIC,
                    m -> {
                        System.out.println("Listener #1: " + m);
                    });

            eventBus.subscribe(
                    ClientSubscriptionManager.GLOBAL_CHAT_TOPIC,
                    m -> {
                        System.out.println("Listener #2: " + m);
                    });

            DatabaseManager databaseManager = new DatabaseManager(config.databaseSettings());

            UserRepository userRepository = new H2UserRepository(databaseManager);

            MessageRepository messageRepository = new H2MessageRepository(databaseManager);

            TokenManager tokenManager = new TokenManager(config.tokenSettings());

            AuthService authService = new AuthServiceImpl(userRepository, tokenManager);

            ChatService chatService =
                    new ChatServiceImpl(messageRepository, userRepository, eventBus);

            User user = authService.register("mate", "password");

            String token = authService.login(user.username(), "password");

            chatService.sendMessage(user.id(), "Hi!");

            for (Message message : chatService.getChatHistory()) {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
