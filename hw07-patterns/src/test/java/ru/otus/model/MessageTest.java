package ru.otus.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MessageTest {
    @Test
    @DisplayName("clone with nullable fields")
    void NullableFieldsClone() {
        var msg = new Message.Builder(1L).build();
        var cloned = msg.clone();

        assertThat(cloned.getField13()).isNull();
    }
}
