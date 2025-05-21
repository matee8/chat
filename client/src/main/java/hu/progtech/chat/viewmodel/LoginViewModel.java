package hu.progtech.chat.viewmodel;

import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.UserSessionService;
import hu.progtech.chat.viewmodel.command.Command;
import hu.progtech.chat.viewmodel.command.LoginCommand;
import hu.progtech.chat.viewmodel.command.RegisterCommand;
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
            final Runnable onLoginSuccess) {
        this.chatService = chatService;
        this.userSessionService = userSessionService;
        this.onLoginSuccess = onLoginSuccess;
        this.loginCommand =
                new LoginCommand(chatService, username, password, errorMessage, onLoginSuccess);
        this.registerCommand = new RegisterCommand(chatService, username, password, errorMessage);
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

    public Command loginCommand() {
        return loginCommand;
    }

    public Command registerCommand() {
        return registerCommand;
    }

    public void resetFields() {
        username.set("");
        password.set("");
        errorMessage.set("");
        LOGGER.debug("LoginViewModel fields reset.");
    }
}
