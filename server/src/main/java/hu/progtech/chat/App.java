package hu.progtech.chat;

import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;

public class App {
    public static void main(String[] args) {
        AppConfig config = AppConfigLoader.loadConfig();
    }
}
