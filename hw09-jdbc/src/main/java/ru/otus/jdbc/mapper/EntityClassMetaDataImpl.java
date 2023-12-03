package ru.otus.jdbc.mapper;

import ru.otus.annotations.Id;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T>{

    private final String name;
    private final Field idField;
    private final List<Field> allFields;

    private final List<Field> fieldsWithpoutId;
    private final Constructor<T> constructor;
    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.name = clazz.getName();
        idField = pickFieldByAnnotation(clazz.getFields(), Id.class).orElseThrow();
        allFields = List.of(clazz.getFields());
        fieldsWithpoutId = allFields.stream().filter(f->!f.equals(idField)).toList();
        constructor = prepareConstructorNoArgs(clazz);
    }
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Constructor<T> getConstructor() {
        return constructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
       return Collections.unmodifiableList(allFields);
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return Collections.unmodifiableList(fieldsWithpoutId);
    }

    private static Optional<Field> pickFieldByAnnotation(Field[] fields, Class<? extends Annotation> annotation) {
        return Arrays.
                stream(fields).
                filter(field -> field.isAnnotationPresent(annotation)).
                findAny();
    }

    private static<T> Constructor<T> prepareConstructorNoArgs(Class<T> clazz)  {
        try {
            var constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor;
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String normalizeFieldName(String name) {
        return name.toLowerCase();
    }
}
