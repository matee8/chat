package hu.progtech.chat.viewmodel.commands;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.service.ChatService;
import java.util.concurrent.Flow;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogoutCommand {
    private static final Logger LOGGER = LogManager.getLogger(LogoutCommand.class);
    private final ChatService chatService;
    private final StringProperty currentUserDisplay;
    private Flow.Subscription messageSubscription;
    private final ObservableList<ChatMessage> messages;
    private final Runnable onLogoutSuccess;

    public LogoutCommand(
            ChatService chatService,
            Flow.Subscription messageSubscription,
            StringProperty currentUserDisplay,
            ObservableList<ChatMessage> messages,
            Runnable onLogoutSuccess) {
        this.chatService = chatService;
        this.messageSubscription = messageSubscription;
        this.currentUserDisplay = currentUserDisplay;
        this.messages = messages;
        this.onLogoutSuccess = onLogoutSuccess;
    }

    public void execute() {
        LOGGER.info("Logout command executed.");
        if (messageSubscription != null) {
            messageSubscription.cancel();
            messageSubscription = null;
            LOGGER.debug("Message subscription cancelled.");
        }
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
