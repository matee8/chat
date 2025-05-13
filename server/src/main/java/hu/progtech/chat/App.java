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
import hu.progtech.chat.util.PasswordHasher;
import hu.progtech.chat.util.TokenManager;

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();

        try {
            DatabaseManager databaseManager = new DatabaseManager(config.databaseSettings());

            UserRepository userRepo = new H2UserRepository(databaseManager);

            String hashedPassword = PasswordHasher.hashPassword("test");

            User user = new User("mate1", hashedPassword);

            user = userRepo.save(user);

            for (User elem : userRepo.findAll()) {
                System.out.println(elem);
                System.out.println(elem.passwordHash());
            }

            MessageRepository messageRepo = new H2MessageRepository(databaseManager);

            Message message = new Message(user.id(), "Sziasztok!");

            messageRepo.save(message);

            for (Message elem : messageRepo.getChatHistory()) {
                System.out.println(elem);
            }

            TokenManager tokenManager = new TokenManager(config.tokenSettings());

            String token = tokenManager.generateToken(user);

            System.out.println(token);

            long userId = tokenManager.validateTokenAndGetClaims(token);

            System.out.println(userId);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
