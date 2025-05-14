package hu.progtech.chat.event;

import hu.progtech.chat.model.Message;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InMemoryChatEventBus implements ChatEventBus {
    private static final Logger LOGGER = LogManager.getLogger(InMemoryChatEventBus.class);
    private final Map<String, List<Subscriber>> subscribersByTopic = new ConcurrentHashMap<>();

    @Override
    public void subscribe(final String topic, final Subscriber subscriber) {
        List<Subscriber> subscribers = subscribersByTopic.get(topic);
        if (subscribers == null) {
            subscribers = new CopyOnWriteArrayList<>();
            subscribersByTopic.put(topic, subscribers);
        }

        subscribers.add(subscriber);

        LOGGER.info("Subscriber {} subscribed to topic '{}'.", subscriber, topic);
    }

    @Override
    public void unsubscribe(final String topic, final Subscriber subscriber) {
        final List<Subscriber> subscribers = subscribersByTopic.get(topic);
        if (subscribers == null) {
            LOGGER.warn(
                    "Subscriber {} tried to unsubscribe from topic '{}', which does not exist.",
                    subscriber,
                    topic);
            return;
        }

        subscribers.remove(subscriber);

        if (subscribers.isEmpty()) {
            subscribersByTopic.remove(topic);
        }

        LOGGER.info("Subscriber {} unsubscribed from topic '{}'.", subscriber, topic);
    }

    @Override
    public void publish(final String topic, final Message message) {
        final List<Subscriber> subscribers = subscribersByTopic.get(topic);

        if (subscribers == null || subscribers.isEmpty()) {
            LOGGER.info(
                    "No subscribers found for topic '{}' when publishing message with ID {}.",
                    topic,
                    message.id());
            return;
        }

        LOGGER.info(
                "Publishing message with ID {} to {} subscriber(s) on topic '{}'.",
                message.id(),
                subscribers.size(),
                topic);

        for (Subscriber listener : subscribers) {
            try {
                listener.onMessage(message);
            } catch (Exception e) {
                LOGGER.error(
                        "Error notifying subscriber {} on topic '{}': {}.",
                        listener,
                        topic,
                        e.getMessage(),
                        e);
            }
        }
    }
}
