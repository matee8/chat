package hu.progtech.chat.config;

public record AppConfig(ServerSettings serverSettings, DatabaseSettings databaseSettings, TokenSettings tokenSettings) {}
