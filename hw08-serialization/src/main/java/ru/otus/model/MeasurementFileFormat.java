package ru.otus.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasurementFileFormat {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("value")
    private final double value;

    @JsonCreator
    public MeasurementFileFormat(
            @JsonProperty("name") String name,
            @JsonProperty("value") double value
    ) {
        this.name = name;
        this.value = value;
    }

    public Measurement getMeasurement(){
        return new Measurement(name, value);
    }
}
