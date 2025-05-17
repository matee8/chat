package hu.progtech.chat.model;

import java.util.Optional;

public record LoginResult(boolean success, String message, Optional<String> token) {}
