package hu.progtech.chat.commands;

import java.io.Serializable;

public interface Command extends Serializable {
    void execute();
}
