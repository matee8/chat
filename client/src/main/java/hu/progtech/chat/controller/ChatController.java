package hu.progtech.chat.controller;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.viewmodel.ChatViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatController implements InitializableController {
    private static final Logger LOGGER = LogManager.getLogger(ChatController.class);

    @FXML private ListView<ChatMessage> messageListView;
    @FXML private TextField messageInputField;
    @FXML private Button sendButton;
    @FXML private Button logoutButton;
    @FXML private Label errorMessageLabel;
    @FXML private Label currentUserLabel;

    private final ChatViewModel viewModel;

    public ChatController(ChatViewModel viewModel) {
        this.viewModel = viewModel;
        LOGGER.debug("ChatController instantiated.");
    }

    @FXML
    public void initialize() {
        LOGGER.debug("Initializing ChatController FXML components.");
        messageListView.setItems(viewModel.messages());
        messageInputField.textProperty().bindBidirectional(viewModel.currentMessageProperty());
        errorMessageLabel.textProperty().bind(viewModel.errorMessageProperty());
        currentUserLabel.textProperty().bind(viewModel.currentUserDisplayProperty());

        sendButton.setOnAction(event -> viewModel.sendMessageCommand().execute());
        logoutButton.setOnAction(event -> viewModel.logoutCommand().execute());

        LOGGER.info("ChatController FXML components initialized and bound.");
    }

    @Override
    public void onViewShown() {
        LOGGER.debug("ChatView shown. Initializing chat session.");
        viewModel.initializeChatSession();
        messageInputField.requestFocus();
    }

    public void onViewHidden() {
        LOGGER.debug("ChatView hidden. Cleaning up chat session resources.");
        viewModel.cleanup();
    }
}
