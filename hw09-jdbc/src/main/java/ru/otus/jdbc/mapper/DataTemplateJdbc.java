package ru.otus.jdbc.mapper;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.core.sessionmanager.DataBaseOperationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData<T> entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;


    public DataTemplateJdbc(DbExecutor dbExecutor, EntitySQLMetaData<T> entitySQLMetaData, EntityClassMetaData entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        var sql = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(
                connection, sql, List.of(id),
                rs -> {
                    try {
                        if (rs.next()) {
                            return entityFromResultSet(entityClassMetaData.getConstructor(), entityClassMetaData.getAllFields(), rs);
                        }
                        return null;
                    } catch (Exception e) {
                        throw new DataBaseOperationException("failed to read rs", e);
                    }
                });
    }

    @Override
    public List<T> findAll(Connection connection) {
        var sql = entitySQLMetaData.getSelectAllSql();
        return dbExecutor.executeSelect(
                connection, sql, Collections.emptyList(),
                rs -> {
                    try {
                        List<T> out = new ArrayList<>();
                        while (rs.next()) {
                            out.add(entityFromResultSet(entityClassMetaData.getConstructor(), entityClassMetaData.getAllFields(), rs));
                        }
                        return out;
                    } catch (Exception e) {
                        throw new DataBaseOperationException("failed to read rs", e);
                    }
                }).orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T client) {
        return executeInsertUpdate(connection, client, entitySQLMetaData.getInsertSql());
    }

    @Override
    public void update(Connection connection, T client) {
        executeInsertUpdate(connection, client, entitySQLMetaData.getUpdateSql());
    }

    private long executeInsertUpdate(Connection connection, T client, String sql) {
        try {
            return dbExecutor.executeStatement(
                    connection, sql, entityArguments(client, entityClassMetaData.getAllFields())
            );
        } catch (Exception e) {
            throw new DataBaseOperationException("failed to insert entity", e);
        }
    }

    private List<Object> entityArguments(T entity, List<Field> fieldsOrder) throws Exception {
        var fieldByName = Arrays.
                stream(entity.getClass().getDeclaredFields()).
                collect(Collectors.toMap(Field::getName, Function.identity()));

        List<Object> values = new ArrayList<>();
        for(var field : fieldsOrder) {
            var entityField = fieldByName.get(field);
            entityField.setAccessible(true);
            values.add(entityField.get(entity));
        }
        return values;
    }

    private T entityFromResultSet(Constructor<T> constructor, List<Field> fields, ResultSet rs)throws Exception{
        Map<String, Object> valuesByColumns = new HashMap<>();
        var meta = rs.getMetaData();
        for (int i = 0; i < meta.getColumnCount(); i++) {
            var columnName = EntityClassMetaDataImpl.normalizeFieldName(meta.getColumnName(i + 1));
            valuesByColumns.put(columnName, rs.getObject(columnName));
        }
        Set<Field> fieldsToFil = fields.stream().collect(Collectors.toSet());

        var entity = constructor.newInstance();
        for (var field : entity.getClass().getDeclaredFields()) {
            if (fieldsToFil.contains(field)) {
                var value = valuesByColumns.get(EntityClassMetaDataImpl.normalizeFieldName(field.getName()));
                field.setAccessible(true);
                field.set(entity, value);
            }
        }
        return entity;
    }

}
