package hu.progtech.chat;

import java.time.LocalDateTime;

class Message {
    private int id;
    private String username;
    private String content;
    private LocalDateTime timestamp;

    public Message(int id, String username, String content, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "]" + username + ": " + content;
    }
}
