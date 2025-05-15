package hu.progtech.chat.service;

import hu.progtech.chat.event.ChatEventBus;
import hu.progtech.chat.model.Message;
import hu.progtech.chat.model.User;
import hu.progtech.chat.repository.MessageRepository;
import hu.progtech.chat.repository.RepositoryException;
import hu.progtech.chat.repository.UserRepository;
import hu.progtech.chat.util.TokenManager;
import hu.progtech.chat.util.TokenValidationException;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatServiceImpl implements ChatService {
    private static final Logger LOGGER = LogManager.getLogger(ChatServiceImpl.class);

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TokenManager tokenManager;
    private final ChatEventBus chatEventBus;

    public ChatServiceImpl(
            final MessageRepository messageRepository,
            final UserRepository userRepository,
            final ChatEventBus chatEventBus,
            final TokenManager tokenManager) {
        if (messageRepository == null) {
            throw new IllegalArgumentException("MessageRepository cannot be null.");
        }

        if (userRepository == null) {
            throw new IllegalArgumentException("UserRepository cannot be null.");
        }

        if (chatEventBus == null) {
            throw new IllegalArgumentException("ChatEventBus cannot be null.");
        }

        if (tokenManager == null) {
            throw new IllegalArgumentException("TokenManager cannot be null.");
        }

        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.chatEventBus = chatEventBus;
        this.tokenManager = tokenManager;
    }

    @Override
    public Message sendMessage(final String token, final String content) throws ServiceException {
        if (content == null || content.isBlank()) {
            throw new ServiceException("Message cannot be empty.");
        }

        final long senderId;
        try {
            senderId = tokenManager.validateTokenAndGetClaims(token);
        } catch (IllegalArgumentException | TokenValidationException e) {
            LOGGER.error("Error extracing ID from token: {}.", e.getMessage(), e);
            throw new ServiceException("Failed to extract ID from token.", e);
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

            chatEventBus.publish("globalRoom", savedMessage);
            LOGGER.debug(
                    "Published message {} to event bus on topic {}.", savedMessage, "globalRoom");
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

    @Override
    public String getUsernameForMessage(final Message message) throws ServiceException {
        try {
            LOGGER.info("Fetching sender username for message.");

            Optional<User> userOptional = userRepository.findById(message.senderId());
            if (userOptional.isEmpty()) {
                LOGGER.warn(
                        "Send message failed: Sender with ID {} not found.", message.senderId());
                throw new ServiceException("Sender user not found. Cannot send message.");
            }
            User user = userOptional.get();

            return user.username();
        } catch (RepositoryException e) {
            LOGGER.error(
                    "Repository error while extracting sender username for User ID {}: {}.",
                    message.senderId(),
                    e.getMessage(),
                    e);
            throw new ServiceException("Failed to extract username due to a system error.", e);
        } catch (IllegalArgumentException e) {
            LOGGER.error(
                    "Invalid argument while extracting sender username for User ID {}: {}.",
                    message.senderId(),
                    e.getMessage(),
                    e);
            throw new ServiceException("Failed to extract username due to a system error.", e);
        }
    }
}
