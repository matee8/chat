package hu.progtech.chat.event;

import hu.progtech.chat.model.Message;

public interface Subscriber {
    void onMessage(Message message);
}
