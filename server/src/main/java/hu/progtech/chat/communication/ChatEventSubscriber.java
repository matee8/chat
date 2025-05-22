package hu.progtech.chat.communication;

import com.google.protobuf.util.Timestamps;
import hu.progtech.chat.event.Subscriber;
import hu.progtech.chat.model.Message;
import hu.progtech.chat.proto.MessageEvent;
import hu.progtech.chat.service.ChatService;
import io.grpc.stub.StreamObserver;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChatEventSubscriber implements Subscriber {
    private static final Logger LOGGER = LogManager.getLogger(ChatEventSubscriber.class);
    private final StreamObserver<MessageEvent> streamObserver;
    private final ChatService chatService;

    public ChatEventSubscriber(
            final StreamObserver<MessageEvent> streamObserver, final ChatService chatService) {
        if (streamObserver == null) {
            throw new IllegalArgumentException("StreamObserver cannot be null.");
        }

        if (chatService == null) {
            throw new IllegalArgumentException("ChatService cannot be null.");
        }

        this.streamObserver = streamObserver;
        this.chatService = chatService;
    }

    @Override
    public void onMessage(final Message message) {
        try {
            String senderUsername = chatService.getUsernameForMessage(message);

            ZonedDateTime zonedDateTime = message.timestamp().atZone(ZoneOffset.UTC);

            long millis = zonedDateTime.toInstant().toEpochMilli();

            MessageEvent messageEvent =
                    MessageEvent.newBuilder()
                            .setMessageId(message.id())
                            .setSenderName(senderUsername)
                            .setContent(message.content())
                            .setTimestamp(Timestamps.fromMillis(millis))
                            .build();

            streamObserver.onNext(messageEvent);
        } catch (Exception e) {
            LOGGER.warn("Something went wrong while broadcasting message to client.");
        }
    }
}
