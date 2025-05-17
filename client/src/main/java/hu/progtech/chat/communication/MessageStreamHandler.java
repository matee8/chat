package hu.progtech.chat.communication;

import hu.progtech.chat.model.ChatMessage;
import hu.progtech.chat.proto.MessageEvent;
import hu.progtech.chat.util.TimestampConverter;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.SubmissionPublisher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageStreamHandler implements StreamObserver<MessageEvent> {
    public static final Logger LOGGER = LogManager.getLogger(MessageStreamHandler.class);

    private final SubmissionPublisher<ChatMessage> publisher;

    public MessageStreamHandler(final SubmissionPublisher<ChatMessage> publisher) {
        this.publisher = publisher;
    }

    @Override
    public void onNext(MessageEvent protoMessage) {
        try {
            LOGGER.info("gRPC stream received message ID: {}.", protoMessage.getMessageId());

            ChatMessage chatMessage =
                    new ChatMessage(
                            protoMessage.getMessageId(),
                            protoMessage.getSenderName(),
                            protoMessage.getContent(),
                            TimestampConverter.toLocalDateTime(protoMessage.getTimestamp()));

            publisher.submit(chatMessage);
            LOGGER.debug("Submitted ChatMessage to publisher: {}.", chatMessage.id());
        } catch (Exception e) {
            LOGGER.error("Error processing incoming message in onNext: {}.", e.getMessage(), e);
        }
    }

    @Override
    public void onError(Throwable t) {
        LOGGER.error("Message stream error from server: {}.", t, t);
        publisher.closeExceptionally(t);
    }

    @Override
    public void onCompleted() {
        LOGGER.info("Message stream completed by server.");
        publisher.close();
    }
}
