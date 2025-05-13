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

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();

        try {
            DatabaseManager.initialize(config);

            UserRepository userRepo = new H2UserRepository();

            User user = new User("mate1", "test");

            user = userRepo.save(user);

            for (User elem : userRepo.findAll()) {
                System.out.println(elem);
            }

            MessageRepository messageRepo = new H2MessageRepository();

            Message message = new Message(1, "Sziasztok!");

            messageRepo.save(message);

            for (Message elem : messageRepo.getChatHistory()) {
                System.out.println(elem);
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
