package hu.progtech.chat.services;

import hu.progtech.chat.events.ChatEventBus;
import hu.progtech.chat.models.Message;
import hu.progtech.chat.models.User;
import hu.progtech.chat.networking.ClientSubscriptionManager;
import hu.progtech.chat.repositories.MessageRepository;
import hu.progtech.chat.repositories.RepositoryException;
import hu.progtech.chat.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatServiceImpl implements ChatService {
    private static final Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatEventBus eventBus;

    public ChatServiceImpl(
            final MessageRepository messageRepository,
            final UserRepository userRepository,
            ChatEventBus eventBus) {
        if (messageRepository == null) {
            throw new IllegalArgumentException("MessageRepository cannot be null.");
        }

        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }

        if (eventBus == null) {
            throw new IllegalArgumentException("ChatEventBus cannot be null.");
        }

        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Message sendMessage(final long senderId, final String content) throws ServiceException {
        if (content == null || content.isBlank()) {
            throw new ServiceException("Message cannot be empty.");
        }

        try {
            LOGGER.info("User ID {} attempting to send message: '{}'.", senderId, content);

            final Optional<User> senderOptional = userRepository.findById(senderId);
            if (senderOptional.isEmpty()) {
                LOGGER.warn("Send message failed: Sender with ID {} not found.", senderId);
                throw new ServiceException("Sender user not found. Cannot send message.");
            }

            final Message newMessage = new Message(senderId, content);
            final Message savedMessage = messageRepository.save(newMessage);

            LOGGER.info(
                    "Message sent successfully by User ID {}. Message ID: {}",
                    senderId,
                    savedMessage.id());

            eventBus.publish(ClientSubscriptionManager.GLOBAL_CHAT_TOPIC, savedMessage);
            LOGGER.debug(
                    "Published message {} to event bus on topic {}.",
                    savedMessage,
                    ClientSubscriptionManager.GLOBAL_CHAT_TOPIC);
            return savedMessage;
        } catch (RepositoryException e) {
            LOGGER.error(
                    "Repository error while sending message for User ID {}: {}.",
                    senderId,
                    e.getMessage(),
                    e);
            throw new ServiceException("Failed to send message due to a system error.", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                    "Invalid argument while creating message for User ID {}: {}.",
                    senderId,
                    e.getMessage(),
                    e);
            throw new ServiceException("Failed to send message due to a system error.", e);
        }
    }

    @Override
    public List<Message> getChatHistory() throws ServiceException {
        try {
            LOGGER.info("Fetching chat history.");

            List<Message> history = messageRepository.getChatHistory();
            LOGGER.debug("Retrieved {} messages for chat history.", history.size());

            return history;
        } catch (RepositoryException e) {
            LOGGER.error("Repository error while fetching chat history: {}.", e.getMessage(), e);
            throw new ServiceException("Failed to send message due to a system error.", e);
        }
    }
}
