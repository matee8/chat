package hu.progtech.chat;

import hu.progtech.chat.communication.RequestHandler;
import hu.progtech.chat.config.AppConfig;
import hu.progtech.chat.config.AppConfigLoader;
import hu.progtech.chat.event.ChatEventBus;
import hu.progtech.chat.event.InMemoryChatEventBus;
import hu.progtech.chat.repository.H2MessageRepository;
import hu.progtech.chat.repository.H2UserRepository;
import hu.progtech.chat.repository.MessageRepository;
import hu.progtech.chat.repository.UserRepository;
import hu.progtech.chat.service.AuthService;
import hu.progtech.chat.service.AuthServiceImpl;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.ChatServiceImpl;
import hu.progtech.chat.util.DatabaseManager;
import hu.progtech.chat.util.TokenManager;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
    private static final Logger LOGGER = LogManager.getLogger(App.class);

    public static void main(String[] args) {
        try {
            AppConfig appConfig = AppConfigLoader.loadConfig();

            TokenManager tokenManager = new TokenManager(appConfig.tokenSettings());

            DatabaseManager databaseManager = new DatabaseManager(appConfig.databaseSettings());
            UserRepository userRepository = new H2UserRepository(databaseManager);
            MessageRepository messageRepository = new H2MessageRepository(databaseManager);

            ChatEventBus chatEventBus = new InMemoryChatEventBus();

            AuthService authService = new AuthServiceImpl(userRepository, tokenManager);
            ChatService chatService =
                    new ChatServiceImpl(
                            messageRepository, userRepository, chatEventBus, tokenManager);

            RequestHandler requestHandler =
                    new RequestHandler(authService, chatService, chatEventBus);

            Server server =
                    ServerBuilder.forPort(appConfig.serverSettings().port())
                            .addService(requestHandler)
                            .build()
                            .start();
            LOGGER.info("gRPC server started, listening on {}.", appConfig.serverSettings().port());

            Runtime.getRuntime()
                    .addShutdownHook(
                            new Thread(
                                    () -> {
                                        LOGGER.info("Shutting down gRPC server...");
                                        server.shutdown();
                                        LOGGER.info("Exiting...");
                                    }));

            server.awaitTermination();
        } catch (IOException e) {
            LOGGER.error("Failed to build/start gRPC server: {}.", e.getMessage(), e);
        } catch (InterruptedException e) {
            LOGGER.error("Server interrupted.", e);
        } finally {
            LOGGER.info("Exiting...");
        }
    }
}
