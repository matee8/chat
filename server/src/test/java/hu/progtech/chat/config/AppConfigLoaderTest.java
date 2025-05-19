package hu.progtech.chat.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class AppConfigLoaderTest {

    @Test
    void loadConfig_loadsConfigurationFromTestFile() {
        AppConfig appConfig = AppConfigLoader.loadConfig();

        assertNotNull(appConfig);

        assertEquals(9091, appConfig.serverSettings().port());

        assertEquals("jdbc:h2:./chat_test_db", appConfig.databaseSettings().url());

        assertEquals("sa_test", appConfig.databaseSettings().username());

        assertEquals("", appConfig.databaseSettings().password());

        assertEquals("test-secret-key", appConfig.tokenSettings().secretKey());

        assertEquals("MyChatApp", appConfig.tokenSettings().issuer());
    }
}
