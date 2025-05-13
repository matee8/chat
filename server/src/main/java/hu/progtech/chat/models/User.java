package hu.progtech.chat.models;

public class User {
    private final long id;
    private final String username;
    private final String passwordHash;
    private final long createdAt;

    public User(long id, String username, String passwordHash, long created_at) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.created_at = created_at;
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
}
