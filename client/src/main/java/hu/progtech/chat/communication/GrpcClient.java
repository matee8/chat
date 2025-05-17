package hu.progtech.chat.communication;

import hu.progtech.chat.proto.ChatServiceGrpc;
import hu.progtech.chat.proto.LoginRequest;
import hu.progtech.chat.proto.LoginResponse;
import hu.progtech.chat.proto.RegisterRequest;
import hu.progtech.chat.proto.RegisterResponse;
import hu.progtech.chat.proto.SendMessageRequest;
import hu.progtech.chat.proto.SendMessageResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GrpcClient {
    private static final Logger LOGGER = LogManager.getLogger(GrpcClient.class);

    private final ChatServiceGrpc.ChatServiceBlockingStub blockingStub;
    private final ChatServiceGrpc.ChatServiceStub asyncStub;

    public GrpcClient(final String host, final int port) {
        ManagedChannel channel =
                ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        this.blockingStub = ChatServiceGrpc.newBlockingStub(channel);
        this.asyncStub = ChatServiceGrpc.newStub(channel);

        LOGGER.info("gRPC client connected to {}:{}", host, port);
    }

    public RegisterResponse register(final RegisterRequest request) throws StatusRuntimeException {
        LOGGER.debug("Sending RegisterRequest: {}", request.getUsername());
        return blockingStub.register(request);
    }

    public LoginResponse login(final LoginRequest request) throws StatusRuntimeException {
        LOGGER.debug("Sending LoginRequest: {}", request.getUsername());
        return blockingStub.login(request);
    }

    public SendMessageResponse sendMessage(SendMessageRequest request)
            throws StatusRuntimeException {
        LOGGER.debug("Sending SendMessageRequest.");
        return blockingStub.sendMessage(request);
    }
}
