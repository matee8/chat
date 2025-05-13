package hu.progtech.chat.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private final long id;
    private final long senderId;
    private final String content;
    private final LocalDateTime timestamp;

    public Message(long id, long senderId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.senderId = senderId;
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Message content cannot be null or blank.");
        }
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(long senderId, String content) {
        this(0L, senderId, content, null);
    }

    public long id() {
        return id;
    }

    public long senderId() {
        return senderId;
    }

    public String content() {
        return content;
    }

    public LocalDateTime timestamp() {
        return timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Message message = (Message) o;
        return id != 0 && id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "Message{id=%d, senderId=%d, content='%s', timestamp=%s",
                id, senderId, content, timestamp);
    }
}
