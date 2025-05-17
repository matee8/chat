package hu.progtech.chat.viewmodel;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.UserSessionService;
import hu.progtech.chat.viewmodel.command.Command;
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

    private Flow.Subscription messageSubscription;

    private final Command sendMessageCommand;
    private final Command logoutCommand;

    public ChatViewModel(
            final ChatService chatService,
            final UserSessionService userSessionService,
            final Runnable onLogoutSuccess,
            final Command sendMessageCommand,
            final Command logoutCommand) {
        this.chatService = chatService;
        this.userSessionService = userSessionService;
        this.onLogoutSuccess = onLogoutSuccess;

        this.sendMessageCommand = sendMessageCommand;
        this.logoutCommand = logoutCommand;
    }

    public ObservableList<ChatMessage> getMessages() {
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

    public void initializeChatSession() {
        if (!userSessionService.isAuthenticated()) {
            LOGGER.warn(
                    "Attempted to initialize chat session while not authenticated. Redirecting to"
                            + " login.");
            Platform.runLater(onLogoutSuccess);
            return;
        }

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

        if (messageSubscription != null) {
            messageSubscription.cancel();
            LOGGER.debug("Cancelled existing message subscription before creating a new one.");
        }

        Flow.Publisher<ChatMessage> messageStream = chatService.subscribeToMessages();

        messageStream.subscribe(new StreamSubscriber(messageSubscription, messages, errorMessage));
    }
}
