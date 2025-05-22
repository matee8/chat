package hu.progtech.chat.viewmodel.command;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.service.ChatService;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogoutCommand implements Command {
    private static final Logger LOGGER = LogManager.getLogger(LogoutCommand.class);
    private final ChatService chatService;
    private final StringProperty currentUserDisplay;
    private final ObservableList<ChatMessage> messages;
    private final Runnable onLogoutSuccess;

    public LogoutCommand(
            final ChatService chatService,
            final StringProperty currentUserDisplay,
            final ObservableList<ChatMessage> messages,
            final Runnable onLogoutSuccess) {
        this.chatService = chatService;
        this.currentUserDisplay = currentUserDisplay;
        this.messages = messages;
        this.onLogoutSuccess = onLogoutSuccess;
    }

    @Override
    public void execute() {
        LOGGER.info("Logout command executed.");
        chatService.logout();
        Platform.runLater(
                () -> {
                    currentUserDisplay.set("Not Logged In");
                    messages.clear();
                    onLogoutSuccess.run();
                });
        LOGGER.info("User logged out, UI updated and navigated to login.");
    }
}
