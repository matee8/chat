package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.repositories.core.DatabaseManager;

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();
        try {
            DatabaseManager.initialize(config);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
