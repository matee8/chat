package hu.progtech.chat.communication;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.model.LoginResult;
import hu.progtech.chat.model.RequestResult;
import hu.progtech.chat.proto.ChatServiceGrpc;
import hu.progtech.chat.proto.LoginRequest;
import hu.progtech.chat.proto.LoginResponse;
import hu.progtech.chat.proto.RegisterRequest;
import hu.progtech.chat.proto.RegisterResponse;
import hu.progtech.chat.proto.SendMessageRequest;
import hu.progtech.chat.proto.SendMessageResponse;
import hu.progtech.chat.proto.SubscribeRequest;
import hu.progtech.chat.service.MessageStreamHandler;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrpcClient implements Client {
    public static final Logger LOGGER = LogManager.getLogger(GrpcClient.class);

    private final ManagedChannel channel;
    private final ChatServiceGrpc.ChatServiceBlockingStub blockingStub;
    private final ChatServiceGrpc.ChatServiceStub asyncStub;

    public GrpcClient(final String host, final int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        this.blockingStub = ChatServiceGrpc.newBlockingStub(channel);
        this.asyncStub = ChatServiceGrpc.newStub(channel);

        LOGGER.info("gRPC client initialized for {}:{}.", host, port);
    }

    @Override
    public CompletableFuture<RequestResult> register(final String username, final String password) {
        LOGGER.debug("Attempting to register user: {}.", username);

        final RegisterRequest request =
                RegisterRequest.newBuilder().setUsername(username).setPassword(password).build();

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        RegisterResponse response = blockingStub.register(request);
                        return new RequestResult(response.getSuccess(), response.getMessage());
                    } catch (Exception e) {
                        LOGGER.error(
                                "Registration failed for user {}: {}",
                                request.getUsername(),
                                e.getMessage(),
                                e);
                        return new RequestResult(false, "Registration failed: " + e.getMessage());
                    }
                });
    }

    @Override
    public CompletableFuture<LoginResult> login(final String username, final String password) {
        LOGGER.debug("Attempting to log in user: {}.", username);

        final LoginRequest request =
                LoginRequest.newBuilder().setUsername(username).setPassword(password).build();

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        LoginResponse response = blockingStub.login(request);

                        Optional<String> tokenOptional = Optional.empty();
                        if (response.getSuccess()) {
                            String token = response.getToken();

                            if (token == null || token.isBlank()) {
                                LOGGER.warn(
                                        "Login response success=true but token is null or blank.");
                            } else {
                                tokenOptional = Optional.of(token);
                            }
                        }

                        return new LoginResult(
                                response.getSuccess(), response.getMessage(), tokenOptional);
                    } catch (Exception e) {
                        LOGGER.error(
                                "Login failed for user {}: {}.",
                                request.getUsername(),
                                e.getMessage(),
                                e);
                        return new LoginResult(
                                false, "Login failed: " + e.getMessage(), Optional.empty());
                    }
                });
    }

    @Override
    public CompletableFuture<RequestResult> sendMessage(final String token, final String content) {
        LOGGER.debug("Attempting to send message.");

        final SendMessageRequest request =
                SendMessageRequest.newBuilder().setToken(token).setContent(content).build();

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        SendMessageResponse response = blockingStub.sendMessage(request);

                        return new RequestResult(response.getSuccess(), response.getMessage());
                    } catch (Exception e) {
                        LOGGER.error("Sending message failed: {}.", e.getMessage());

                        return new RequestResult(false, "Send message failed: " + e.getMessage());
                    }
                });
    }

    @Override
    public Flow.Publisher<ChatMessage> subscribeToMessages(final String token) {
        LOGGER.debug("Subscribing to message stream.");

        final SubscribeRequest request = SubscribeRequest.newBuilder().setToken(token).build();

        SubmissionPublisher<ChatMessage> publisher = new SubmissionPublisher<>();
        MessageStreamHandler streamHandler = new MessageStreamHandler(publisher);

        try {
            asyncStub.subscribe(request, streamHandler);
        } catch (Exception e) {
            LOGGER.error("Error initating message subscription: {}.", e.getMessage(), e);
            streamHandler.onError(e);
        }

        return publisher;
    }

    @Override
    public void close() throws Exception {
        LOGGER.info(
                "Shutting down gRPC channel for {}:{}.", channel.authority(), channel.toString());

        if (!channel.isTerminated()) {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }

        LOGGER.info("gRPC channel shutdown complete.");
    }
}
