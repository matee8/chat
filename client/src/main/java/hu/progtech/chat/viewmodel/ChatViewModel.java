package hu.progtech.chat.viewmodel;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.concurrent.Flow;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.viewmodel.command.Command;

public class ChatViewModel {
    private static final Logger LOGGER = LogManager.getLogger(ChatViewModel.class);

    private final ObservableList<ChatMessage> messages = FXCollections.observableArrayList();

    private final StringProperty currentMessage = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final StringProperty currentUserDisplay = new SimpleStringProperty("");

    private final ChatService chatService;
    private final Runnable onLogoutSuccess;

    private Flow.Subscription messageSubscription;

    private final Command sendMessageCommand;
    private final Command logoutCommand;

    public ChatViewModel(final ChatService chatService, final Runnable onLogoutSuccess, final Command sendMessageCommand, final Command logoutCommand) {
        this.chatService = chatService;
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
}
