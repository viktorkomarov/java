package ru.otus.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.model.MeasurementFileFormat;
import ru.otus.model.Measurement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ResourcesFileLoader implements Loader {
    private final String fileName;
    private final ObjectMapper mapper = new ObjectMapper();

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Measurement> load() {
        try (var measurementsFile = new FileInputStream(ClassLoader.getSystemClassLoader().getResource(fileName).getFile())){
            var listType =
                    mapper.getTypeFactory().constructCollectionType(ArrayList.class, MeasurementFileFormat.class);
            List<MeasurementFileFormat> entities =  mapper.readValue(measurementsFile, listType);
            return entities.stream().map(MeasurementFileFormat::getMeasurement).toList();
        } catch (IOException ex) {
            throw new FileProcessException(ex);
        }

    }
}
