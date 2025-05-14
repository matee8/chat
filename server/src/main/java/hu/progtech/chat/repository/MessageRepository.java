package hu.progtech.chat.repository;

import hu.progtech.chat.model.Message;
import java.util.List;

public interface MessageRepository {
    Message save(Message message) throws RepositoryException;

    List<Message> getChatHistory() throws RepositoryException;
}
