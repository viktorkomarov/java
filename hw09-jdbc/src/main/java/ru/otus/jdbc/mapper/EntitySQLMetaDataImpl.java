package ru.otus.jdbc.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData<T>{
    private final EntityClassMetaData<T> meta;
    private final List<String> columns;
    public EntitySQLMetaDataImpl(EntityClassMetaData<T> meta) {
        this.meta = meta;
        this.columns = meta.
                getAllFields().
                stream().
                map(f->EntityClassMetaDataImpl.normalizeFieldName(f.getName())).
                toList();
    }

    @Override
    public String getSelectAllSql() {
        return new StringBuilder("SELECT ").
                append(String.join(",", columns)).
                append("FROM ").
                append(meta.getName()).
                toString();
    }

    @Override
    public String getSelectByIdSql() {
        var getAllSql = getSelectAllSql();
        return new StringBuilder( getSelectAllSql()).
                append("WHERE ").
                append(idFiledColumn()).
                append("= ?").
                toString();
    }

    @Override
    public String getInsertSql() {
        var args = Collections.nCopies(columns.size(), "?");
        var columnsNames = String.join(",", columns);
        var argsColumns = String.join(",", args);
        return new StringBuilder("INSERT INTO ").
                append(meta.getName()).
                append("(").append(columnsNames).append(") ").
                append("VALUES ").
                append("(").append(argsColumns).append(") ").
                toString();
    }

    @Override
    public String getUpdateSql() {
        var updatedArgs = meta.
                getFieldsWithoutId().
                stream().
                map(f -> EntityClassMetaDataImpl.normalizeFieldName(f.getName()) + " = ?").
                toList();

        return new StringBuilder("UPDATE ").
                append(meta.getName()).
                append(" SET ").
                append(String.join(", ", updatedArgs)).
                append(" WHERE ").
                append(idFiledColumn()).
                append("= ?").
                toString();

    }

    private String idFiledColumn() {
       return EntityClassMetaDataImpl.normalizeFieldName(meta.getIdField().getName());
    }
}
