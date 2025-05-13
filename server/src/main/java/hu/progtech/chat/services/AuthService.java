package hu.progtech.chat.services;

import hu.progtech.chat.models.User;

public interface AuthService {
    User register(String username, String password) throws ServiceException;

    String login(String username, String password) throws ServiceException;
}
