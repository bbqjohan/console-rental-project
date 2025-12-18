package org.example.registry;

import com.fasterxml.jackson.core.type.TypeReference;

import org.example.entity.item.Item;
import org.example.registry.table.EntityTable;

import java.io.IOException;
import java.util.List;

public final class ItemRegistry extends EntityTable<Item> {
    public ItemRegistry() {
        super("inventory.json");
    }

    @Override
    public void read() throws IOException {
        update(read(new TypeReference<List<Item>>() {}));
    }
}
