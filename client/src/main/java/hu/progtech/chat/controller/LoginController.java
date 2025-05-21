package hu.progtech.chat.controller;

import hu.progtech.chat.viewmodel.LoginViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoginController implements InitializableController {
    private static final Logger LOGGER = LogManager.getLogger(LoginController.class);

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Label errorMessageLabel;

    private final LoginViewModel viewModel;

    public LoginController(LoginViewModel viewModel) {
        this.viewModel = viewModel;
        LOGGER.debug("LoginController instantiated.");
    }

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing LoginController FXML components.");
        usernameField.textProperty().bindBidirectional(viewModel.usernameProperty());
        passwordField.textProperty().bindBidirectional(viewModel.passwordProperty());
        errorMessageLabel.textProperty().bind(viewModel.errorMessageProperty());

        loginButton.setOnAction(event -> viewModel.loginCommand().execute());
        registerButton.setOnAction(event -> viewModel.registerCommand().execute());
        LOGGER.info("LoginController FXML components initialized and bound.");
    }

    @Override
    public void onViewShown() {
        LOGGER.debug("LoginView shown. Resetting fields.");
        viewModel.resetFields();
        usernameField.requestFocus();
    }
}
