package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.MeasurementFileFormat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class FileSerializer implements Serializer {
    private final String fileName;
    private final ObjectMapper mapper = new ObjectMapper();

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    private SortedMap<String, Double> toMeasurementFileFormat(Map<String, Double> data) {
        var sortedKeys = data.keySet().stream().sorted().toList();
        SortedMap<String,Double> pairs = new TreeMap<>();
        for (var key : sortedKeys) {
            pairs.put(key, data.get(key));
        }
        return pairs;
    }


    @Override
    public void serialize(Map<String, Double> data) {
        var fileFormatData = toMeasurementFileFormat(data);
        try (var outputFile = new FileOutputStream(fileName)){
            mapper.writeValue(outputFile, fileFormatData);
        } catch(IOException ex) {
            throw new FileProcessException(ex);
        }
    }
}
