package hu.progtech.chat.viewmodel.commands;

import hu.progtech.chat.service.ChatService;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SendMessageCommand {
    private static final Logger LOGGER = LogManager.getLogger(SendMessageCommand.class);
    private final ChatService chatService;
    private final StringProperty currentMessage;
    private final StringProperty errorMessage;

    public SendMessageCommand(
            ChatService chatService, StringProperty currentMessage, StringProperty errorMessage) {
        this.chatService = chatService;
        this.currentMessage = currentMessage;
        this.errorMessage = errorMessage;
    }

    public void execute() {
        String messageContent = currentMessage.get();
        if (messageContent == null || messageContent.trim().isEmpty()) {
            Platform.runLater(() -> errorMessage.set("Message cannot be empty."));
            LOGGER.warn("Send message attempt with empty content.");
            return;
        }
        LOGGER.info("Send message command executed with content: {}", messageContent);
        chatService
                .sendMessage(messageContent)
                .thenAcceptAsync(
                        response -> {
                            if (response.success()) {
                                Platform.runLater(
                                        () -> {
                                            currentMessage.set("");
                                            errorMessage.set("");
                                        });
                                LOGGER.debug("Message sent successfully.");
                            } else {
                                Platform.runLater(
                                        () ->
                                                errorMessage.set(
                                                        "Send Failed: " + response.message()));
                                LOGGER.warn("Failed to send message: {}", response.message());
                            }
                        },
                        Platform::runLater)
                .exceptionally(
                        ex -> {
                            LOGGER.error(
                                    "Sending message exceptionally failed: {}",
                                    ex.getMessage(),
                                    ex);
                            Platform.runLater(
                                    () -> errorMessage.set("Send Error: " + ex.getMessage()));
                            return null;
                        });
    }
}
