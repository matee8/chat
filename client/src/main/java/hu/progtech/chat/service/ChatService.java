package hu.progtech.chat.service;

import hu.progtech.chat.communication.Client;
import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.model.RequestResult;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatService {
    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    private final Client client;
    private final UserSessionService userSessionService;

    public ChatService(final Client client, final UserSessionService userSessionService) {
        this.client = client;
        this.userSessionService = userSessionService;
    }

    public CompletableFuture<RequestResult> register(final String username, final String password) {
        LOGGER.info("Registering user: {}.", username);

        return client.register(username, password)
                .thenApply(
                        result -> {
                            if (result.success()) {
                                LOGGER.info("Registration successful for user: {}.", username);
                            } else {
                                LOGGER.warn(
                                        "Registreation failed for user {}: {}.",
                                        username,
                                        result.message());
                            }

                            return result;
                        });
    }

    public CompletableFuture<RequestResult> login(final String username, final String password) {
        LOGGER.info("Logging in user: {}.", username);

        return client.login(username, password)
                .thenApply(
                        result -> {
                            if (result.success() && result.token().isPresent()) {
                                userSessionService.login(result.token().get(), username);
                                LOGGER.info("Login succesful for user: {}.", username);

                                return new RequestResult(true, result.message());
                            } else {
                                LOGGER.warn(
                                        "Login failed for user {}: {}.",
                                        username,
                                        result.message());

                                String message = result.message();

                                if (result.success() && result.token().isEmpty()) {
                                    message =
                                            "Login reported success by server, but token was"
                                                    + " missing.";
                                }

                                return new RequestResult(false, message);
                            }
                        });
    }

    public void logout() {
        userSessionService.logout();

        LOGGER.info("User logout process initiated from ChatService.");
        ;
    }

    public CompletableFuture<RequestResult> sendMessage(final String content) {
        final Optional<String> tokenOptional = userSessionService.token();
        if (tokenOptional.isEmpty()) {
            LOGGER.warn("Send message attempt by unauthenticated user.");

            return CompletableFuture.completedFuture(
                    new RequestResult(false, "User not authenticated. Please login first."));
        }

        final String token = tokenOptional.get();

        return client.sendMessage(token, content)
                .thenApply(
                        response -> {
                            if (response.success()) {
                                LOGGER.info("Message sent successfully.");
                            } else {
                                LOGGER.warn("Failed to send message: {}.", response.message());
                            }

                            return response;
                        });
    }

    public Flow.Publisher<ChatMessage> subscribeToMessages() {
        final Optional<String> tokenOptional = userSessionService.token();
        if (tokenOptional.isEmpty()) {
            LOGGER.warn("Subscribe attempt by unauthenticated user.");

            final SubmissionPublisher<ChatMessage> errorPublisher = new SubmissionPublisher<>();
            errorPublisher.closeExceptionally(new IllegalStateException("User not authenticated."));

            return errorPublisher;
        }

        final String token = tokenOptional.get();

        LOGGER.info("Subscribing to messages.");

        return client.subscribeToMessages(token);
    }
}
