package hu.progtech.chat.model;

import java.time.LocalDateTime;

public record Message(long id, long senderId, String content, LocalDateTime timestamp) {
    public Message(long senderId, String content) {
        this(0L, senderId, content, null);
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
    public String toString() {
        return String.format(
                "Message{id=%d, senderId=%d, content='%s', timestamp=%s}",
                id, senderId, content, timestamp);
    }
}
