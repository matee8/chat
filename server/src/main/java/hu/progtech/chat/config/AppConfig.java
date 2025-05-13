package hu.progtech.chat.config;

public final class AppConfig {
    private final ServerSettings serverSettings;
    private final DatabaseSettings databaseSettings;

    public AppConfig(ServerSettings serverSettings, DatabaseSettings databaseSettings) {
        this.serverSettings = serverSettings;
        this.databaseSettings = databaseSettings;
    }

    public ServerSettings server() {
        return serverSettings;
    }

    public DatabaseSettings database() {
        return databaseSettings;
    }

    @Override
    public String toString() {
        return "AppConfig{"
                + "serverSettings = '"
                + serverSettings
                + "', databaseSettings = '"
                + databaseSettings
                + "'}";
    }
}
