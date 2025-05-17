package hu.progtech.chat.model;

import java.time.LocalDateTime;

public record ChatMessage(long id, String senderName, String content, LocalDateTime timestamp) {}
