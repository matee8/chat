package hu.progtech.chat.event;

import hu.progtech.chat.model.Message;

public interface ChatEventBus {
    void subscribe(String topic, Subscriber listener);

    void unsubscribe(String topic, Subscriber listener);

    void publish(String topic, Message message);
}
