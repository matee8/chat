package hu.progtech.chat.events;

import hu.progtech.chat.models.Message;
import java.util.function.Consumer;

public interface ChatEventBus {
    void subscribe(String topic, Consumer<Message> listener);

    void unsubscribe(String topic, Consumer<Message> listener);

    void publish(String topic, Message message);
}
