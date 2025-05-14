package hu.progtech.chat.events;

import hu.progtech.chat.models.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InMemoryChatEventBus implements ChatEventBus {
    private static final Logger LOGGER = LogManager.getLogger(InMemoryChatEventBus.class);
    private final Map<String, List<Consumer<Message>>> listenersByTopic = new ConcurrentHashMap<>();

    @Override
    public void subscribe(final String topic, final Consumer<Message> listener) {
        List<Consumer<Message>> listeners = listenersByTopic.get(topic);
        if (listeners == null) {
            listeners = new CopyOnWriteArrayList<>();
            listenersByTopic.put(topic, listeners);
        }

        listeners.add(listener);

        LOGGER.info("Listener {} subscribed to topic '{}'.", listener, topic);
    }

    @Override
    public void unsubscribe(final String topic, final Consumer<Message> listener) {
        final List<Consumer<Message>> listeners = listenersByTopic.get(topic);
        if (listeners == null) {
            LOGGER.warn(
                    "Listener {} tried to unsubscribe from topic '{}', which does not exist.",
                    listener,
                    topic);
            return;
        }

        listeners.remove(listener);

        if (listeners.isEmpty()) {
            listenersByTopic.remove(topic);
        }

        LOGGER.info("Listener {} unsubscribed from topic '{}'.", listener, topic);
    }

    @Override
    public void publish(final String topic, final Message message) {
        final List<Consumer<Message>> topicListeners = listenersByTopic.get(topic);

        if (topicListeners == null || topicListeners.isEmpty()) {
            LOGGER.info(
                    "No listeners found for topic '{}' when publishing message with ID {}.",
                    topic,
                    message.id());
            return;
        }

        LOGGER.info(
                "Publishing message with ID {} to {} listener(s) on topic '{}'.",
                message.id(),
                topicListeners.size(),
                topic);

        for (Consumer<Message> listener : topicListeners) {
            try {
                listener.accept(message);
            } catch (Exception e) {
                LOGGER.error(
                        "Error notifying listener {} on topic '{}': {}.",
                        listener,
                        topic,
                        e.getMessage(),
                        e);
            }
        }
    }
}
