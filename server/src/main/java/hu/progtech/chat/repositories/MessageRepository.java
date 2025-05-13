package hu.progtech.chat.repositories;

import hu.progtech.chat.models.Message;
import java.util.List;

public interface MessageRepository {
    void save(Message message);

    List<Message> getChatHistory();
}
