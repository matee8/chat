package hu.progtech.chat.commands;

import hu.progtech.chat.ClientHandler;
import java.io.Serializable;

public interface Command extends Serializable {
    void execute(ClientHandler handler);
}
