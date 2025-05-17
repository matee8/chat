package hu.progtech.chat.service;

import hu.progtech.chat.communication.GrpcClient;
import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.model.RequestResult;
import hu.progtech.chat.proto.LoginRequest;
import hu.progtech.chat.proto.RegisterRequest;
import hu.progtech.chat.proto.SendMessageRequest;
import hu.progtech.chat.proto.SubscribeRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatService {
    private static final Logger LOGGER = LogManager.getLogger(ChatService.class);

    private final GrpcClient grpcClient;
    private final UserSessionService userSessionService;

    public ChatService(final GrpcClient grpcClient, final UserSessionService userSessionService) {
        this.grpcClient = grpcClient;
        this.userSessionService = userSessionService;
    }

    public CompletableFuture<RequestResult> register(final String username, final String password) {
        final RegisterRequest request =
                RegisterRequest.newBuilder().setUsername(username).setPassword(password).build();

        return grpcClient
                .register(request)
                .thenApply(
                        response -> {
                            return new RequestResult(response.getSuccess(), response.getMessage());
                        });
    }

    public CompletableFuture<RequestResult> login(final String username, final String password) {
        final LoginRequest request =
                LoginRequest.newBuilder().setUsername(username).setPassword(password).build();

        return grpcClient
                .login(request)
                .thenApply(
                        response -> {
                            if (response.getSuccess()) {
                                userSessionService.login(response.getToken(), username);
                            }

                            return new RequestResult(response.getSuccess(), response.getMessage());
                        });
    }

    public CompletableFuture<RequestResult> sendMessage(final String content) {
        if (!userSessionService.isAuthenticated()) {
            LOGGER.warn("Send message attempt by unauthenticated user.");

            return CompletableFuture.completedFuture(
                    new RequestResult(false, "User not authenticated."));
        }

        final String token = userSessionService.token();

        final SendMessageRequest request =
                SendMessageRequest.newBuilder().setToken(token).setContent(content).build();

        return grpcClient
                .sendMessage(request)
                .thenApply(
                        response -> {
                            return new RequestResult(response.getSuccess(), response.getMessage());
                        });
    }

    public Flow.Publisher<ChatMessage> subscribeToMessages() {
        if (!userSessionService.isAuthenticated()) {
            LOGGER.warn("Subscribe attempt by unauthenticated user.");

            final SubmissionPublisher<ChatMessage> errorPublisher = new SubmissionPublisher<>();
            errorPublisher.closeExceptionally(new IllegalStateException("User not authenticated."));

            return errorPublisher;
        }

        SubmissionPublisher<ChatMessage> publisher = new SubmissionPublisher<>();

        final MessageStreamHandler protoStreamHandler =
                new MessageStreamHandler(publisher);

        final String token = userSessionService.token();

        final SubscribeRequest request = SubscribeRequest.newBuilder().setToken(token).build();

        grpcClient.subscribeToMessage(request, protoStreamHandler);

        return publisher;
    }
}
