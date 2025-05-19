package hu.progtech.chat.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import hu.progtech.chat.model.Message;
import hu.progtech.chat.model.User;
import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class H2MessageRepositoryTest extends H2BaseRepositoryTest {

    private UserRepository userRepository;
    private MessageRepository messageRepository;

    private User testSender;

    @BeforeEach
    void setUp() throws SQLException {
        super.setUpH2Base();

        userRepository = new H2UserRepository(databaseManager);

        messageRepository = new H2MessageRepository(databaseManager);

        testSender = userRepository.save(new User("test-sender", "test-password"));
    }

    @Test
    void save_shouldPersistMessage() {
        Message newMessage = new Message(testSender.id(), "test-msg");
        Message savedMessage = messageRepository.save(newMessage);

        assertNotNull(savedMessage);
        assertTrue(savedMessage.id() > 0);
        assertEquals(testSender.id(), savedMessage.id());
        assertEquals("test-msg", savedMessage.content());
        assertNotNull(savedMessage.timestamp());
    }

    @AfterEach
    void cleanUp() throws SQLException {
        super.cleanUpH2Base();
    }
}
