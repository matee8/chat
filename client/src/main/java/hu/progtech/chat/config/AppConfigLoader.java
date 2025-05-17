package hu.progtech.chat.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppConfigLoader {
    private static final Logger LOGGER = LogManager.getLogger(AppConfigLoader.class);

    private AppConfigLoader() {}

    public static AppConfig loadConfig() throws ConfigurationLoadingException {
        LOGGER.info("Attempting to load application configuration.");

        final Config rawConfig = ConfigFactory.load();
        try {
            final String serverHost = rawConfig.getString("server.host");
            final int serverPort = rawConfig.getInt("server.port");
            final ServerSettings serverSettings = new ServerSettings(serverHost, serverPort);
            LOGGER.debug("Loaded ServerSettings: {}.", serverSettings);

            final AppConfig appConfig = new AppConfig(serverSettings);
            LOGGER.info("Application configuration loaded succesfully.");

            return appConfig;
        } catch (ConfigException.Missing e) {
            LOGGER.error("Missing required configuration property: {}.", e.getMessage(), e);
            throw new ConfigurationLoadingException("Missing required configuration property.", e);
        } catch (ConfigException.BadValue e) {
            LOGGER.error("Invalid configuration value: {}.", e.getMessage(), e);
            throw new ConfigurationLoadingException("Invalid configuration value.", e);
        } catch (Exception e) {
            LOGGER.error("An unexpected error occurred during configuration loading.", e);
            throw new ConfigurationLoadingException("Configuration loading failed.", e);
        }
    }
}
