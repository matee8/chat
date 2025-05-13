package hu.progtech.chat.config;

public final class ServerSettings {
    private final int port;

    public ServerSettings(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "ServerSettings{" + " port = '" + port + "'" + "}";
    }
}
