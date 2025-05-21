import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final Runnable loginCommand;
    private final Runnable registerCommand;
    private final Runnable sendMessageCommand;
    private final Runnable logoutCommand;

    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty errorMessage;
    private final StringProperty currentMessage;
    private final StringProperty currentUserDisplay;

    private final ChatService chatService;
    private final Runnable onLoginSuccess;
    private final Runnable onLogoutSuccess;

    private MessageSubscription messageSubscription;
    private final ObservableList<Message> messages;

    public ChatController(
            StringProperty username,
            StringProperty password,
            StringProperty errorMessage,
            StringProperty currentMessage,
            StringProperty currentUserDisplay,
            ChatService chatService,
            Runnable onLoginSuccess,
            Runnable onLogoutSuccess,
            MessageSubscription messageSubscription,
            ObservableList<Message> messages
    ) {
        this.username = username;
        this.password = password;
        this.errorMessage = errorMessage;
        this.currentMessage = currentMessage;
        this.currentUserDisplay = currentUserDisplay;
        this.chatService = chatService;
        this.onLoginSuccess = onLoginSuccess;
        this.onLogoutSuccess = onLogoutSuccess;
        this.messageSubscription = messageSubscription;
        this.messages = messages;

        this.loginCommand = () -> {
            logger.info("Login command executed for user: {}", username.get());
            chatService
                    .login(username.get(), password.get())
                    .thenAcceptAsync(response -> {
                        if (response.getSuccess()) {
                            logger.info("Login successful for user: {}", username.get());
                            Platform.runLater(() -> {
                                errorMessage.set("");
                                onLoginSuccess.run();
                            });
                        } else {
                            logger.warn("Login failed for user {}: {}", username.get(), response.getMessage());
                            Platform.runLater(() -> errorMessage.set("Login Failed: " + response.getMessage()));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        logger.error("Login exceptionally failed for user {}: {}", username.get(), ex.getMessage(), ex);
                        Platform.runLater(() -> errorMessage.set("Login Error: " + ex.getMessage()));
                        return null;
                    });
        };

        this.registerCommand = () -> {
            logger.info("Register command executed for user: {}", username.get());
            chatService
                    .register(username.get(), password.get())
                    .thenAcceptAsync(response -> {
                        if (response.getSuccess()) {
                            logger.info("Registration successful for user: {}", username.get());
                            Platform.runLater(() -> errorMessage.set("Registration Successful! Please login."));
                        } else {
                            logger.warn("Registration failed for user {}: {}", username.get(), response.getMessage());
                            Platform.runLater(() -> errorMessage.set("Registration Failed: " + response.getMessage()));
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        logger.error("Registration exceptionally failed for user {}: {}", username.get(), ex.getMessage(), ex);
                        Platform.runLater(() -> errorMessage.set("Registration Error: " + ex.getMessage()));
                        return null;
                    });
        };

        this.sendMessageCommand = () -> {
            String messageContent = currentMessage.get();
            if (messageContent == null || messageContent.trim().isEmpty()) {
                Platform.runLater(() -> errorMessage.set("Message cannot be empty."));
                logger.warn("Send message attempt with empty content.");
                return;
            }
            logger.info("Send message command executed with content: {}", messageContent);
            chatService
                    .sendMessage(messageContent)
                    .thenAcceptAsync(response -> {
                        if (response.getSuccess()) {
                            Platform.runLater(() -> {
                                currentMessage.set("");
                                errorMessage.set("");
                            });
                            logger.debug("Message sent successfully.");
                        } else {
                            Platform.runLater(() -> errorMessage.set("Send Failed: " + response.getMessage()));
                            logger.warn("Failed to send message: {}", response.getMessage());
                        }
                    }, Platform::runLater)
                    .exceptionally(ex -> {
                        logger.error("Sending message exceptionally failed: {}", ex.getMessage(), ex);
                        Platform.runLater(() -> errorMessage.set("Send Error: " + ex.getMessage()));
                        return null;
                    });
        };

        this.logoutCommand = () -> {
            logger.info("Logout command executed.");
            if (messageSubscription != null) {
                messageSubscription.cancel();
                messageSubscription = null;
                logger.debug("Message subscription cancelled.");
            }
            chatService.logout();
            Platform.runLater(() -> {
                currentUserDisplay.set("Not Logged In");
                messages.clear();
                onLogoutSuccess.run();
            });
            logger.info("User logged out, UI updated and navigated to login.");
        };
    }

    public Runnable getLoginCommand() {
        return loginCommand;
    }

    public Runnable getRegisterCommand() {
        return registerCommand;
    }

    public Runnable getSendMessageCommand() {
        return sendMessageCommand;
    }

    public Runnable getLogoutCommand() {
        return logoutCommand;
    }
}
