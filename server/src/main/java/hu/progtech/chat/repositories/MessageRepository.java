package hu.progtech.chat.repositories;

import hu.progtech.chat.models.Message;
import java.util.List;

public interface MessageRepository {
    Message save(Message message) throws RepositoryException;

    List<Message> getChatHistory() throws RepositoryException;
}
