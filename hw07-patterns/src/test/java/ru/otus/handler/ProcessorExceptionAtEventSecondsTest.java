package ru.otus.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.homework.TimeProvider;
import ru.otus.processor.homework.ProcessorExceptionAtEvenSeconds;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalTime;

public class ProcessorExceptionAtEventSecondsTest {
    @Test
    @DisplayName("Exception happens")
    void ExceptionHappens(){
        var provider = mock(TimeProvider.class);
        when(provider.now()).thenReturn(LocalTime.of(20,20, 20));
        var processor = new ProcessorExceptionAtEvenSeconds(provider);

        assertThrows(IllegalStateException.class, ()->processor.process(new Message.Builder(1).build()));
    }


    @Test
    @DisplayName("Exception doesn't happen")
    void NoException(){
        var provider = mock(TimeProvider.class);
        when(provider.now()).thenReturn(LocalTime.of(20,20, 21));
        var processor = new ProcessorExceptionAtEvenSeconds(provider);

        // no exceptions
        var msg = processor.process(new Message.Builder(1).build());
    }
}
