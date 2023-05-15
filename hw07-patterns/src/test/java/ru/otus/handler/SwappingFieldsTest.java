package ru.otus.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.homework.ProcessorSwappingField11Field12;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SwappingFieldsTest {
    @Test
    @DisplayName("Simple swapping processor logic")
    void TestSwappingFields(){
        var field11Data = "field11";
        var field12Data = "field12";
        var swappingProcessor = new ProcessorSwappingField11Field12();
        var message = new Message.Builder(1).
                field11(field11Data).
                field12(field12Data).
                build();

        var messageAfterProcessing = swappingProcessor.process(message);

        assertThat(messageAfterProcessing.getField11()).isEqualTo(field12Data);
        assertThat(messageAfterProcessing.getField12()).isEqualTo(field11Data);
    }
}
