package hu.progtech.chat.communication;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.model.LoginResult;
import hu.progtech.chat.model.RequestResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public interface Client extends AutoCloseable {
    CompletableFuture<RequestResult> register(String username, String password);

    CompletableFuture<LoginResult> login(String username, String password);

    CompletableFuture<RequestResult> sendMessage(String token, String content);

    Flow.Publisher<ChatMessage> subscribeToMessages(String token);

    @Override
    void close();
}
