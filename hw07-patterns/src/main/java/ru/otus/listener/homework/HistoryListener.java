package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.model.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HistoryListener implements Listener, HistoryReader {
    private final Map<Long, Message> messagesById;

    public HistoryListener(){
        messagesById = new HashMap<>();
    }

    @Override
    public void onUpdated(Message msg) {
        messagesById.put(msg.getId(), msg.clone());
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(messagesById.get(id));
    }
}
