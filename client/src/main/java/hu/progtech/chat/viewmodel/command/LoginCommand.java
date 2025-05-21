package hu.progtech.chat.viewmodel.command;

import hu.progtech.chat.service.ChatService;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(LoginCommand.class);
    private final ChatService chatService;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty errorMessage;
    private final Runnable onLoginSuccess;

    public LoginCommand(
            final ChatService chatService,
            final StringProperty username,
            final StringProperty password,
            final StringProperty errorMessage,
            final Runnable onLoginSuccess) {
        this.chatService = chatService;
        this.username = username;
        this.password = password;
        this.errorMessage = errorMessage;
        this.onLoginSuccess = onLoginSuccess;
    }

    @Override
    public void execute() {
        LOGGER.info("Login command executed for user: {}", username.get());
        chatService
                .login(username.get(), password.get())
                .thenAcceptAsync(
                        response -> {
                            if (response.success()) {
                                LOGGER.info("Login successful for user: {}", username.get());
                                Platform.runLater(
                                        () -> {
                                            errorMessage.set("");
                                            onLoginSuccess.run();
                                        });
                            } else {
                                LOGGER.warn(
                                        "Login failed for user {}: {}",
                                        username.get(),
                                        response.message());
                                Platform.runLater(
                                        () ->
                                                errorMessage.set(
                                                        "Login Failed: " + response.message()));
                            }
                        },
                        Platform::runLater)
                .exceptionally(
                        ex -> {
                            LOGGER.error(
                                    "Login exceptionally failed for user {}: {}",
                                    username.get(),
                                    ex.getMessage(),
                                    ex);
                            Platform.runLater(
                                    () -> errorMessage.set("Login Error: " + ex.getMessage()));
                            return null;
                        });
    }
}
