package org.example.registry.table;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class EntityTable<T extends Entity<T>> implements Table<T> {
    private final String tablePath;
    private Map<Long, T> table;
    private long idCount;

    public EntityTable(String tablePath) {
        this.idCount = 0;
        this.tablePath = tablePath;
        this.table = new TreeMap<>();
    }

    public EntityTable(String tablePath, Map<Long, T> table, long idCount) {
        this.tablePath = tablePath;
        this.table = table;
        this.idCount = idCount;
    }

    @Override
    public T get(long id) {
        assertPositiveId(id);

        return table.get(id).copy();
    }

    @Override
    public void set(T entity) {
        long id = entity.getId();

        assertPositiveId(id);
        assertEntity(id);

        table.put(id, entity);
    }

    @Override
    public T remove(long id) {
        assertPositiveId(id);
        assertEntity(id);

        return table.remove(id);
    }

    @Override
    public Table<T> add(T entity) {
        long id = ++idCount;
        T copy = entity.copy();
        copy.setId(id);

        table.put(id, copy);

        return this;
    }

    public List<T> getAll() {
        return table.values().stream().map(Copyable::copy).collect(Collectors.toList());
    }

    //    public List<T> readWithType() throws IOException {
    //        List<T> list = new ArrayList<>();
    //
    //        try {
    //            list =
    //                    new ObjectMapper()
    //                            .readValue(new File(getTablePath()), new TypeReference<List<T>>()
    // {});
    //            update(list);
    //        } catch (IOException e) {
    //            throw new IOException("Could not read from table file: " + getTablePath(), e);
    //        }
    //
    //        return list;
    //    }

    public abstract void read() throws IOException;

    public void write() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            List<T> list = new ArrayList<>(table.values());
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(tablePath), list);
        } catch (IOException e) {
            throw new IOException("Could not write to table file: " + tablePath, e);
        }
    }

    protected void update(List<T> file) throws IOException {
        TreeMap<Long, T> newTable = new TreeMap<>();
        long newIdCount = 0;

        for (T entity : file) {
            long id = entity.getId();

            if (newTable.containsKey(id)) {
                throw new IOException(
                        "Couldn't populate the table with data. Encountered entities with duplicate ids: "
                                + id);
            }

            if (id > newIdCount) {
                newIdCount = id;
            }

            newTable.put(id, entity);
        }

        table = newTable;
        idCount = newIdCount;
    }

    protected void assertPositiveId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Negative id is not allowed. Id: " + id);
        }
    }

    protected void assertEntity(long id) {
        if (!table.containsKey(id)) {
            throw new NoEntityException("Member with id " + id + " does not exist.");
        }
    }

    public String getTablePath() {
        return tablePath;
    }
}
