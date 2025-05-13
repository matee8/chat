package hu.progtech.chat.config;

public final class DatabaseSettings {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseSettings(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "DatabaseSettings{url = '"
                + url
                + "', username = '"
                + username
                + "', password = '"
                + password
                + "'}";
    }
}
