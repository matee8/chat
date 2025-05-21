package hu.progtech.chat;

import hu.progtech.chat.communication.Client;
import hu.progtech.chat.communication.GrpcClient;
import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.controller.ChatController;
import hu.progtech.chat.controller.LoginController;
import hu.progtech.chat.controller.ViewManager;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.UserSessionService;
import hu.progtech.chat.viewmodel.ChatViewModel;
import hu.progtech.chat.viewmodel.LoginViewModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App extends Application {
    private static final Logger logger = LogManager.getLogger(App.class);
    private Client client;
    private ViewManager viewManager;

    @Override
    public void start(Stage stage) {
        logger.info("Application starting...");

        AppConfig appConfig = AppConfigLoader.loadConfig();
        logger.info(
                "Configuration loaded. gRPC server: {}:{}",
                appConfig.serverSettings().host(),
                appConfig.serverSettings().port());

        client =
                new GrpcClient(
                        appConfig.serverSettings().host(), appConfig.serverSettings().port());
        UserSessionService userSessionService = new UserSessionService();
        ChatService chatService = new ChatService(client, userSessionService);

        LoginViewModel loginViewModel =
                new LoginViewModel(chatService, userSessionService, this::showChatView);
        ChatViewModel chatViewModel =
                new ChatViewModel(chatService, userSessionService, this::showLoginView);

        viewManager = new ViewManager(stage);

        LoginController loginController = new LoginController(loginViewModel);
        ChatController chatController = new ChatController(chatViewModel);

        viewManager.addScene("login", "/LoginView.fxml", loginController);
        viewManager.addScene("chat", "/ChatView.fxml", chatController);

        showLoginView();

        stage.setTitle("ProgTech Chat Client");
        stage.setOnCloseRequest(
                event -> {
                    logger.info("Application shutting down...");
                    client.close();
                    Platform.exit();
                    System.exit(0);
                });
        stage.show();
        logger.info("Application started successfully.");
    }

    private void showLoginView() {
        Platform.runLater(() -> viewManager.activate("login"));
        logger.info("Switched to Login View");
    }

    private void showChatView() {
        Platform.runLater(() -> viewManager.activate("chat"));
        logger.info("Switched to Chat View");
    }

    @Override
    public void stop() throws Exception {
        logger.info("Application stop method called.");
        if (client != null) {
            client.close();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}
