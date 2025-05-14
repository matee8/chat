package hu.progtech.chat.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final long id;
    private final String username;
    private final String passwordHash;
    private final LocalDateTime createdAt;

    public User(long id, String username, String passwordHash, LocalDateTime createdAt) {
        this.id = id;

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank.");
        }
        this.username = username;

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank.");
        }
        this.passwordHash = passwordHash;

        this.createdAt = createdAt;
    }

    public User(String username, String passwordHash) {
        this(0L, username, passwordHash, null);
    }

    public long id() {
        return id;
    }

    public String username() {
        return username;
    }

    public String passwordHash() {
        return passwordHash;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id != 0 && id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format(
                "User{id=%d, username='%s', passwordHash='[REDACTED]', createdAt=%s",
                id, username, createdAt);
    }
}
