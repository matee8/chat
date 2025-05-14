package hu.progtech.chat.events;

import hu.progtech.chat.models.Message;
import java.util.function.Consumer;

public interface EventBus {
    void subscribe(String topic, Consumer<Message> listener);

    void unsubscibe(String topic, Consumer<Message> listener);

    void publish(String topic, Message message);
}
