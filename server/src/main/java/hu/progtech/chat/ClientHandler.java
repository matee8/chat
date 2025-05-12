package hu.progtech.chat;

import hu.progtech.chat.commands.Command;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(ClientHandler.class);

    private final Socket socket;

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());

            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Command) {
                    Command cmd = (Command) obj;
                    cmd.execute();
                }
            }
        } catch (EOFException eof) {
            logger.info("Client {} disconnected", usernameOrRemote());
        } catch (Exception ex) {
            logger.error("Error in handler for {}", usernameOrRemote(), ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                logger.error("Error closing socket", ex);
            }
        }
    }

    private String usernameOrRemote() {
        return username != null ? username : socket.getRemoteSocketAddress().toString();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
