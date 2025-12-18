package org.example.registry.table;

import java.io.IOException;

public interface Table<T extends Entity<T>> {

    T get(long id);

    void set(T entity);

    T remove(long id);

    Table<T> add(T entity);

    void read() throws IOException;

    void write() throws IOException;
}
