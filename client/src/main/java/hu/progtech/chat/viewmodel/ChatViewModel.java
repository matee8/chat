package hu.progtech.chat.viewmodel;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.UserSessionService;
import hu.progtech.chat.viewmodel.command.Command;
import hu.progtech.chat.viewmodel.command.LogoutCommand;
import hu.progtech.chat.viewmodel.command.SendMessageCommand;
import java.util.Optional;
import java.util.concurrent.Flow;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatViewModel {
    private static final Logger LOGGER = LogManager.getLogger(ChatViewModel.class);

    private final ObservableList<ChatMessage> messages = FXCollections.observableArrayList();

    private final StringProperty currentMessage = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty currentUserDisplay = new SimpleStringProperty("");

    private final ChatService chatService;
    private final UserSessionService userSessionService;
    private final Runnable onLogoutSuccess;

    private final Command sendMessageCommand;
    private final Command logoutCommand;

    public ChatViewModel(
            final ChatService chatService,
            final UserSessionService userSessionService,
            final Runnable onLogoutSuccess) {
        this.chatService = chatService;
        this.userSessionService = userSessionService;
        this.onLogoutSuccess = onLogoutSuccess;

        this.sendMessageCommand = new SendMessageCommand(chatService, currentMessage, errorMessage);
        this.logoutCommand =
                new LogoutCommand(chatService, currentUserDisplay, messages, onLogoutSuccess);
    }

    public ObservableList<ChatMessage> messages() {
        return messages;
    }

    public StringProperty currentMessageProperty() {
        return currentMessage;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public StringProperty currentUserDisplayProperty() {
        return currentUserDisplay;
    }

    public Command sendMessageCommand() {
        return sendMessageCommand;
    }

    public Command logoutCommand() {
        return logoutCommand;
    }

    public void initializeChatSession() {
        Optional<String> usernameOptional = userSessionService.username();
        if (usernameOptional.isEmpty()) {
            LOGGER.warn(
                    "Attempted to initialize chat session while not authenticated. Redirecting to"
                            + " login.");
            Platform.runLater(onLogoutSuccess);
            return;
        }
        String username = usernameOptional.get();

        Platform.runLater(() -> currentUserDisplay.set("Logged in as: " + username));
        LOGGER.info("Initializing chat session for user: {}.", username);
        messages.clear();

        Flow.Publisher<ChatMessage> messageStream = chatService.subscribeToMessages();

        messageStream.subscribe(new StreamSubscriber(messages, errorMessage));
    }
}
