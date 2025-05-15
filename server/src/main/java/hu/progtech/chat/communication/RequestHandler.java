package hu.progtech.chat.communication;

import hu.progtech.chat.event.ChatEventBus;
import hu.progtech.chat.event.Subscriber;
import hu.progtech.chat.proto.ChatServiceGrpc;
import hu.progtech.chat.proto.LoginRequest;
import hu.progtech.chat.proto.LoginResponse;
import hu.progtech.chat.proto.MessageEvent;
import hu.progtech.chat.proto.RegisterRequest;
import hu.progtech.chat.proto.RegisterResponse;
import hu.progtech.chat.proto.SendMessageRequest;
import hu.progtech.chat.proto.SendMessageResponse;
import hu.progtech.chat.proto.SubscribeRequest;
import hu.progtech.chat.service.AuthService;
import hu.progtech.chat.service.ChatService;
import hu.progtech.chat.service.ServiceException;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestHandler extends ChatServiceGrpc.ChatServiceImplBase {
    private static final Logger LOGGER = LogManager.getLogger(RequestHandler.class);
    private static final String GLOBAL_CHAT_ROOM = "globalChat";

    private final AuthService authService;
    private final ChatService chatService;
    private final ChatEventBus chatEventBus;

    public RequestHandler(
            final AuthService authService,
            final ChatService chatService,
            final ChatEventBus chatEventBus) {
        this.authService = authService;
        this.chatService = chatService;
        this.chatEventBus = chatEventBus;
    }

    @Override
    public void register(final RegisterRequest req, StreamObserver<RegisterResponse> out) {
        try {
            authService.register(req.getUsername(), req.getPassword());
            out.onNext(
                    RegisterResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Registered successfully")
                            .build());
        } catch (ServiceException e) {
            out.onNext(
                    RegisterResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(e.getMessage())
                            .build());
        }
        out.onCompleted();
    }

    @Override
    public void login(final LoginRequest req, StreamObserver<LoginResponse> out) {
        try {
            String token = authService.login(req.getUsername(), req.getPassword());
            out.onNext(
                    LoginResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Login successful")
                            .setToken(token)
                            .build());
        } catch (ServiceException e) {
            out.onNext(
                    LoginResponse.newBuilder()
                            .setSuccess(false)
                            .setMessage(e.getMessage())
                            .build());
        }
        out.onCompleted();
    }

    @Override
    public void sendMessage(final SendMessageRequest req, StreamObserver<SendMessageResponse> out) {
        try {
            chatService.sendMessage(req.getToken(), req.getContent());
            out.onNext(
                    SendMessageResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage("Message successfully sent")
                            .build());
        } catch (ServiceException e) {
            out.onNext(
                    SendMessageResponse.newBuilder()
                            .setSuccess(true)
                            .setMessage(e.getMessage())
                            .build());
        }
        out.onCompleted();
    }

    @Override
    public void subscribe(
            final SubscribeRequest req, StreamObserver<MessageEvent> responseObserver) {
        Subscriber subscriber = new ChatEventSubscriber(responseObserver, chatService);

        ServerCallStreamObserver<MessageEvent> serverObs =
                (ServerCallStreamObserver<MessageEvent>) responseObserver;
        serverObs.setOnCancelHandler(
                () -> {
                    chatEventBus.unsubscribe(GLOBAL_CHAT_ROOM, subscriber);
                    LOGGER.info("Client has unsubscribed from message events.");
                });

        chatEventBus.subscribe(GLOBAL_CHAT_ROOM, subscriber);
        LOGGER.info("New client subscribed to message events.");
    }
}
