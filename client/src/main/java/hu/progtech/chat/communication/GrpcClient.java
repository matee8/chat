package hu.progtech.chat.communication;

import hu.progtech.chat.proto.ChatServiceGrpc;
import hu.progtech.chat.proto.LoginRequest;
import hu.progtech.chat.proto.LoginResponse;
import hu.progtech.chat.proto.MessageEvent;
import hu.progtech.chat.proto.RegisterRequest;
import hu.progtech.chat.proto.RegisterResponse;
import hu.progtech.chat.proto.SendMessageRequest;
import hu.progtech.chat.proto.SendMessageResponse;
import hu.progtech.chat.proto.SubscribeRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrpcClient {
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

    public CompletableFuture<RegisterResponse> register(final RegisterRequest request) {
        LOGGER.debug("Attempting to register user: {}.", request.getUsername());

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return blockingStub.register(request);
                    } catch (Exception e) {
                        LOGGER.error(
                                "Registration failed for user {}: {}",
                                request.getUsername(),
                                e.getMessage(),
                                e);
                        return RegisterResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("Registration failed: " + e.getMessage())
                                .build();
                    }
                });
    }

    public CompletableFuture<LoginResponse> login(final LoginRequest request) {
        LOGGER.debug("Attempting to log in user: {}.", request.getUsername());

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return blockingStub.login(request);
                    } catch (Exception e) {
                        LOGGER.error(
                                "Login failed for user {}: {}.",
                                request.getUsername(),
                                e.getMessage(),
                                e);
                        return LoginResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("Login failed: " + e.getMessage())
                                .build();
                    }
                });
    }

    public CompletableFuture<SendMessageResponse> sendMessage(final SendMessageRequest request) {
        LOGGER.debug("Attempting to send message.");

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return blockingStub.sendMessage(request);
                    } catch (Exception e) {
                        LOGGER.error("Sending message failed: {}.", e.getMessage());
                        return SendMessageResponse.newBuilder()
                                .setSuccess(false)
                                .setMessage("Sending message failed: " + e.getMessage())
                                .build();
                    }
                });
    }

    public void subscribeToMessage(
            final SubscribeRequest request, final StreamObserver<MessageEvent> responseObserver) {
        LOGGER.debug("Subscribing to message stream.");

        try {
            asyncStub.subscribe(request, responseObserver);
        } catch (Exception e) {
            LOGGER.error("Error initating message subscription: {}.", e.getMessage(), e);
            responseObserver.onError(e);
        }
    }
}
