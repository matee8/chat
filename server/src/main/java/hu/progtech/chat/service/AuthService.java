package hu.progtech.chat.service;

import hu.progtech.chat.model.User;

public interface AuthService {
    User register(String username, String password) throws ServiceException;

    String login(String username, String password) throws ServiceException;
}
