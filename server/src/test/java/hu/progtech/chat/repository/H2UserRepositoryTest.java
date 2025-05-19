package hu.progtech.chat.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hu.progtech.chat.model.User;
import java.sql.SQLException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class H2UserRepositoryTest extends H2BaseRepositoryTest {
    private UserRepository userRepository;

    @BeforeEach
    void setUp() throws SQLException {
        super.setUpH2Base();
        userRepository = new H2UserRepository(databaseManager);
    }

    @Test
    void save_shouldPersistUser() {
        User newUser = new User("test-user", "test-password");
        User savedUser = userRepository.save(newUser);

        assertNotNull(savedUser);

        assertTrue(savedUser.id() > 0);

        assertEquals("test-user", savedUser.username());

        assertNotNull(savedUser.createdAt());

        Optional<User> foundUser = userRepository.findByUsername("test-user");

        assertTrue(foundUser.isPresent());

        assertEquals(savedUser.id(), foundUser.get().id());
    }
}
