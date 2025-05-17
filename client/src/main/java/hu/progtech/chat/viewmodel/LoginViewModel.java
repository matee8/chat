package hu.progtech.chat.viewmodel;

import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.UserSessionService;
import hu.progtech.chat.viewmodel.command.Command;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginViewModel {
    private static final Logger LOGGER = LogManager.getLogger(LoginViewModel.class);

    private final StringProperty username = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final StringProperty errorMessage = new SimpleStringProperty("");

    private final ChatService chatService;
    private final UserSessionService userSessionService;
    private final Runnable onLoginSuccess;

    private final Command loginCommand;
    private final Command registerCommand;

    public LoginViewModel(
            final ChatService chatService,
            final UserSessionService userSessionService,
            final Runnable onLoginSuccess,
            final Command loginCommand,
            final Command registerCommand) {
        this.chatService = chatService;
        this.userSessionService = userSessionService;
        this.onLoginSuccess = onLoginSuccess;
        this.loginCommand = loginCommand;
        this.registerCommand = registerCommand;
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public void resetFields() {
        username.set("");
        password.set("");
        errorMessage.set("");
        LOGGER.debug("LoginViewModel fields reset.");
    }
}
