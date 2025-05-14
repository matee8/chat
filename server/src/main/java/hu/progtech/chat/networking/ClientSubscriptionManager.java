package hu.progtech.chat.networking;

import hu.progtech.chat.events.ChatEventBus;
import hu.progtech.chat.models.Message;
import hu.progtech.chat.models.User;
import hu.progtech.chat.repositories.UserRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;

public class ClientSubscriptionManager {
    private static final Logger LOGGER = LogManager.getLogger(ClientSubscriptionManager.class);
    public static final String GLOBAL_CHAT_TOPIC = "globalChatroom";

    private final Map<WebSocket, ClientSession> activeSessions = new ConcurrentHashMap<>();
    private final Map<WebSocket, Consumer<Message>> messageListeners = new ConcurrentHashMap<>();
    private final ChatEventBus eventBus;
    private final UserRepository userRepository;

    public ClientSubscriptionManager(
            final ChatEventBus eventBus, final UserRepository userRepository) {
        this.eventBus = eventBus;
        this.userRepository = userRepository;
    }

    public void registerClient(final ClientSession session) {
        activeSessions.put(session.connection(), session);
        LOGGER.info("Client registered: {}.", session.connection().getRemoteSocketAddress());
    }

    public ClientSession session(final WebSocket connection) {
        return activeSessions.get(connection);
    }

    public void subscribeToChat(final ClientSession session) {
        if (!session.isAuthenticated()) {
            LOGGER.warn(
                    "Attempted to subscribe unauthenticated session: {}.",
                    session.connection().getRemoteSocketAddress());
            return;
        }

        Consumer<Message> listener = createMessageListenerForClient(session);
        messageListeners.put(session.connection(), listener);
        eventBus.subscribe(GLOBAL_CHAT_TOPIC, listener);
        LOGGER.info("Client {} subscribed to topic: {}.", session.userId(), GLOBAL_CHAT_TOPIC);
    }

    private Consumer<Message> createMessageListenerForClient(final ClientSession session) {
        return message -> {
            try {
                final Optional<User> userOptional = userRepository.findById(session.userId());
                if (userOptional.isEmpty()) {
                    LOGGER.warn("Unkown user (ID {}).", session.userId());
                }
                final String username = userOptional.get().username();

                // TODO: actual protobuf implementations here
                final String broadcast = message.content();

                session.connection().send(broadcast);

                LOGGER.info("Sent message broadcast to client {}.", session.userId());
            } catch (Exception e) {
                LOGGER.error(
                        "Error sending message to client {}: {}.",
                        session.userId(),
                        e.getMessage(),
                        e);
            }
        };
    }

    public void unregisterClient(WebSocket connection) {
        final ClientSession session = activeSessions.remove(connection);
        if (session != null) {
            Consumer<Message> listener = messageListeners.remove(connection);
            if (listener != null) {
                eventBus.unsubscribe(GLOBAL_CHAT_TOPIC, listener);
            }

            LOGGER.info(
                    "Client unregistered and unsubscribed: {}.",
                    connection.getRemoteSocketAddress());
        }
    }
}
