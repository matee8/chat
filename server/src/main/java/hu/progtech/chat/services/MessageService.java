package hu.progtech.chat.services;

import hu.progtech.chat.models.Message;
import java.util.List;

public interface MessageService {
    Message sendMessage(String userId, String content) throws ServiceException;

    List<Message> getChatHistory() throws ServiceException;
}
