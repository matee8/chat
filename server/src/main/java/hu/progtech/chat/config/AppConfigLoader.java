package hu.progtech.chat.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfigLoader {
    private static final Logger logger = LogManager.getLogger(AppConfigLoader.class);

    private AppConfigLoader() {}

    public static AppConfig loadConfig() throws RuntimeException {
        logger.info("Attempting to load application configuration.");

        Config rawConfig = ConfigFactory.load();
        try {
            int serverPort = rawConfig.getInt("server.port");
            ServerSettings serverSettings = new ServerSettings(serverPort);
            logger.debug("Loaded ServerSettings: {}", serverSettings);

            String dbUrl = rawConfig.getString("db.url");
            String dbUsername = rawConfig.getString("db.username");
            String dbPassword = rawConfig.getString("db.password");
            DatabaseSettings databaseSettings = new DatabaseSettings(dbUrl, dbUsername, dbPassword);
            logger.debug("Loaded DatabaseSettings: {}", databaseSettings);

            AppConfig appConfig = new AppConfig(serverSettings, databaseSettings);
            logger.info("Application configuration loaded.");

            return appConfig;
        } catch (ConfigException.Missing e) {
            logger.fatal("Missing required configuration property: " + e.getMessage(), e);
            throw new RuntimeException("Missing required configuration property.", e);
        } catch (ConfigException.BadValue e) {
            logger.fatal("Invalid configuration value: " + e.getMessage(), e);
            throw new RuntimeException("Invalid configuration value.", e);
        } catch (Exception e) {
            logger.fatal("An unexpected error occurred during configuration loading.", e);
            throw new RuntimeException("Configuration loading failed.", e);
        }
    }
}
