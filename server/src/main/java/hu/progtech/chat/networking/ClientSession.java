package hu.progtech.chat.networking;

import org.java_websocket.WebSocket;

public record ClientSession(WebSocket connection, long userId) {
    public boolean isAuthenticated() {
        return userId != 0;
    }
}
