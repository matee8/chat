package hu.progtech.chat.models;

public class Message {
    private final long id;
    private final String sender;
    private final String recipient;
    private final String content;
    private final long timestamp;

    public Message(long id, String sender, String recipient, String content, long timestamp) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
