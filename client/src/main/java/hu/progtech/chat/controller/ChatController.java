package hu.progtech.chat.controller;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.viewmodel.ChatViewModel;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatController implements InitializableController {
    private static final Logger LOGGER = LogManager.getLogger(ChatController.class);
    private static final DateTimeFormatter CHAT_MESSAGE_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss");

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

        messageListView.setCellFactory(
                param ->
                        new ListCell<ChatMessage>() {
                            @Override
                            protected void updateItem(ChatMessage item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty || item == null) {
                                    setText(null);
                                } else {
                                    LocalDateTime utcTimestamp = item.timestamp();
                                    LocalDateTime localTimestamp =
                                            utcTimestamp
                                                    .atZone(ZoneOffset.UTC)
                                                    .withZoneSameInstant(ZoneId.systemDefault())
                                                    .toLocalDateTime();

                                    setText(
                                            "["
                                                    + item.senderName()
                                                    + " at "
                                                    + localTimestamp.format(CHAT_MESSAGE_FORMATTER)
                                                    + "]: "
                                                    + item.content());
                                }
                            }
                        });

        LOGGER.info("ChatController FXML components initialized and bound.");
    }

    @Override
    public void onViewShown() {
        LOGGER.debug("ChatView shown. Initializing chat session.");
        viewModel.initializeChatSession();
        messageInputField.requestFocus();
    }
}
