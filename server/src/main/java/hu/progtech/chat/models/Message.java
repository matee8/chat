package hu.progtech.chat.models;

import java.util.Objects;

public class Message {
    private final long id;
    private final String sender;
    private final String content;
    private final long timestamp;

    public Message(long id, String sender, String content, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
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
        return "Message{"
                + " id = '"
                + id
                + "', "
                + " sender = '"
                + sender
                + "', "
                + " content = '"
                + content
                + "', "
                + " timestamp = '"
                + timestamp
                + "'"
                + "}";
    }
}
