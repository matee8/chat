package hu.progtech.chat.model;

import java.time.LocalDateTime;

public record ChatMessage(long id, String senderName, String content, LocalDateTime timestamp) {
    @Override
    public String toString() {
        return "[" + senderName + " at " + timestamp + "]: " + content;
    }
}
