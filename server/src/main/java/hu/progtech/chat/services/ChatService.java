package hu.progtech.chat.services;

import hu.progtech.chat.models.Message;
import java.util.List;

public interface ChatService {
    Message sendMessage(long senderId, String content) throws ServiceException;

    List<Message> getChatHistory() throws ServiceException;
}
