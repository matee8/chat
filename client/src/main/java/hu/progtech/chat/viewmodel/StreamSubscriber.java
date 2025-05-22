package hu.progtech.chat.viewmodel;

import hu.progtech.chat.model.ChatMessage;
import java.util.concurrent.Flow;
import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StreamSubscriber implements Flow.Subscriber<ChatMessage> {
    public static final Logger LOGGER = LogManager.getLogger(StreamSubscriber.class);

    private Flow.Subscription messageSubscription;
    private final ObservableList<ChatMessage> messages;
    private final StringProperty errorMessage;

    public StreamSubscriber(
            final ObservableList<ChatMessage> messages, final StringProperty errorMessage) {
        this.messages = messages;
        this.errorMessage = errorMessage;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        messageSubscription = subscription;
        messageSubscription.request(Long.MAX_VALUE);

        LOGGER.info("Subscribed to message stream. Requested messages.");
    }

    @Override
    public void onNext(ChatMessage item) {
        Platform.runLater(
                () -> {
                    messages.add(item);
                    LOGGER.debug("Added message to UI: {}.", item.id());
                });
    }

    @Override
    public void onError(Throwable throwable) {
        LOGGER.error("Error in message stream: {}.", throwable, throwable);
        Platform.runLater(
                () -> {
                    errorMessage.set("Connection error: " + throwable.getMessage());
                });
    }

    @Override
    public void onComplete() {
        LOGGER.info("Message stream completed.");
        Platform.runLater(
                () -> {
                    errorMessage.set("Disconnected from chat server.");
                });
    }
}
