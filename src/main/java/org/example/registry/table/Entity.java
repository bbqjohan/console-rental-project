package org.example.registry.table;

public abstract class Entity<T> implements Copyable<T> {
    private long id;

    public Entity() {}

    public Entity(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
