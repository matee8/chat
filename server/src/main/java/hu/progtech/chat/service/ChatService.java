package hu.progtech.chat.service;

import hu.progtech.chat.model.Message;
import java.util.List;

public interface ChatService {
    Message sendMessage(String token, String content) throws ServiceException;

    List<Message> getChatHistory() throws ServiceException;

    String getUsernameForMessage(Message message) throws ServiceException;
}
