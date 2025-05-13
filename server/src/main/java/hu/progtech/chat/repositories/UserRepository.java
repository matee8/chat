package hu.progtech.chat.repositories;

import hu.progtech.chat.models.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(long id);

    Optional<User> findByUsername(String username);

    List<User> findAll();
}
