package hu.progtech.chat.util;

import hu.progtech.chat.proto.Request;
import hu.progtech.chat.proto.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ProtoManager {
    private ProtoManager() {}

    public static Request readChatRequest(InputStream inputStream) throws IOException {
        byte[] requestLength = inputStream.readNBytes(4);
        if (requestLength.length < 4) {
            throw new IOException("Stream closed.");
        }
        int length = ByteBuffer.wrap(requestLength).getInt();
        byte[] data = inputStream.readNBytes(length);
        return Request.parseFrom(data);
    }

    public static void writeChatResponse(OutputStream outputStream, Response response)
            throws IOException {
        byte[] payload = response.toByteArray();
        byte[] header = ByteBuffer.allocate(4).putInt(payload.length).array();
        outputStream.write(header);
        outputStream.write(payload);
        outputStream.flush();
    }
}
