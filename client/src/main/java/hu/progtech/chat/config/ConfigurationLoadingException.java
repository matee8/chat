package hu.progtech.chat.config;

public class ConfigurationLoadingException extends RuntimeException {
    public ConfigurationLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationLoadingException(String message) {
        super(message);
    }
}
