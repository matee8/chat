package hu.progtech.chat.services;

import java.util.List;

import hu.progtech.chat.models.Message;

public interface MessageService {
    Message sendMessage(String userId, String content) throws ServiceException;

    List<Message> getChatHistory() throws ServiceException;
}
