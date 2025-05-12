package hu.progtech.chat.commands;

import java.io.Serializable;
import hu.progtech.chat.ClientHandler;

public interface Command extends Serializable {
    void execute(ClientHandler handler);
}
