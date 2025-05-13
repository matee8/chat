package hu.progtech.chat.config;

public final class AppConfig {
    private final ServerSettings serverSettings;
    private final DatabaseSettings databaseSettings;
    private final TokenSettings tokenSettings;

    public AppConfig(
            ServerSettings serverSettings,
            DatabaseSettings databaseSettings,
            TokenSettings tokenSettings) {
        this.serverSettings = serverSettings;
        this.databaseSettings = databaseSettings;
        this.tokenSettings = tokenSettings;
    }

    public ServerSettings server() {
        return serverSettings;
    }

    public DatabaseSettings database() {
        return databaseSettings;
    }

    public TokenSettings token() {
        return tokenSettings;
    }

    @Override
    public String toString() {
        return "AppConfig{serverSettings = '"
                + serverSettings
                + "', databaseSettings = '"
                + databaseSettings
                + "', tokenSettings = '"
                + tokenSettings
                + "'}";
    }
}
