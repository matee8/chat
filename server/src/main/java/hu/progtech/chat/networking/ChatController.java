package hu.progtech.chat.networking;

import hu.progtech.chat.services.ChatService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatController {
    private static final Logger LOGGER = LogManager.getLogger(ChatController.class);
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    public void handleIncomingChatMessage(long senderId) {}
}
