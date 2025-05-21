package hu.progtech.chat.viewmodel.commands;

import hu.progtech.chat.service.ChatService;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegisterCommand {
    private static final Logger LOGGER = LogManager.getLogger(RegisterCommand.class);
    private final ChatService chatService;
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty errorMessage;

    public RegisterCommand(
            ChatService chatService,
            StringProperty username,
            StringProperty password,
            StringProperty errorMessage) {
        this.chatService = chatService;
        this.username = username;
        this.password = password;
        this.errorMessage = errorMessage;
    }

    public void execute() {
        LOGGER.info("Register command executed for user: {}", username.get());
        chatService
                .register(username.get(), password.get())
                .thenAcceptAsync(
                        response -> {
                            if (response.success()) {
                                LOGGER.info("Registration successful for user: {}", username.get());
                                Platform.runLater(
                                        () ->
                                                errorMessage.set(
                                                        "Registration Successful! Please login."));
                            } else {
                                LOGGER.warn(
                                        "Registration failed for user {}: {}",
                                        username.get(),
                                        response.message());
                                Platform.runLater(
                                        () ->
                                                errorMessage.set(
                                                        "Registration Failed: "
                                                                + response.message()));
                            }
                        },
                        Platform::runLater)
                .exceptionally(
                        ex -> {
                            LOGGER.error(
                                    "Registration exceptionally failed for user {}: {}",
                                    username.get(),
                                    ex.getMessage(),
                                    ex);
                            Platform.runLater(
                                    () ->
                                            errorMessage.set(
                                                    "Registration Error: " + ex.getMessage()));
                            return null;
                        });
    }
}
