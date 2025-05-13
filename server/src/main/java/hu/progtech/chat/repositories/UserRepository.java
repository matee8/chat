package hu.progtech.chat.repositories;

import hu.progtech.chat.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user) throws RepositoryException;

    Optional<User> findById(long id) throws RepositoryException;

    Optional<User> findByUsername(String username) throws RepositoryException;

    List<User> findAll() throws RepositoryException;
}
