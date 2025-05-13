package hu.progtech.chat.config;

public record DatabaseSettings(String url, String username, String password) {
    @Override
    public String toString() {
        return String.format("DatabaseSettings{url='%s', username='%s', password='[REDACTED]'}", url, username);
    }
}
