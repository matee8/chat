package hu.progtech.chat.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message {
    private long id;
    private final long senderId;
    private final String content;
    private LocalDateTime timestamp;

    public Message(long senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public Message(long id, long senderId, String content, LocalDateTime timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSenderId() {
        return senderId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id == message.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Message{id = '"
                + id
                + "', sender = '"
                + senderId
                + "', content = '"
                + content
                + "', timestamp = '"
                + timestamp
                + "'}";
    }
}
