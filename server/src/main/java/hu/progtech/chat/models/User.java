package hu.progtech.chat.models;

import java.util.Objects;

public class User {
    private final long id;
    private final String username;
    private final String passwordHash;
    private final long createdAt;

    public User(long id, String username, String passwordHash, long createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{"
                + " id = '"
                + id
                + "', "
                + " username = '"
                + username
                + "', "
                + " createdAt = '"
                + createdAt
                + "'"
                + "}";
    }
}
