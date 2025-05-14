package hu.progtech.chat.communication;

import hu.progtech.chat.proto.ChatServiceGrpc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RequestHandler extends ChatServiceGrpc.ChatServiceImplBase {
    private static final Logger logger = LogManager.getLogger(RequestHandler.class);
}
