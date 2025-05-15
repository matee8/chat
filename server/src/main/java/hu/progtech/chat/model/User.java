package hu.progtech.chat.model;

import java.time.LocalDateTime;

public record User(long id, String username, String passwordHash, LocalDateTime createdAt) {
    public User(String username, String passwordHash) {
        this(0L, username, passwordHash, null);
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
    public String toString() {
        return String.format(
                "User{id=%d, username='%s', passwordHash='[REDACTED]', createdAt=%s",
                id, username, createdAt);
    }
}
