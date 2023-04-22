package ru.otus.processor.homework;

import ru.otus.model.Message;
import ru.otus.processor.Processor;

public class ProcessorExceptionAtEvenSeconds implements Processor {
    private final TimeProvider timeProvider;

    public ProcessorExceptionAtEvenSeconds(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    @Override
    public Message process(Message message) {
        var currentSecond = timeProvider.now().getSecond();
        if (currentSecond % 2 == 0) {
            throw new IllegalStateException("currentSecond is even " + currentSecond);
        }
        return message;
    }
}
