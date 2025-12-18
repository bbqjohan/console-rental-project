package org.example.registry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entity.item.Item;
import org.example.registry.table.EntityTable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class ItemRegistry extends EntityTable<Item> {
    public ItemRegistry() {
        super("inventory.json");
    }

    @Override
    public void read() throws IOException {
        try {
            List<Item> response =
                    new ObjectMapper()
                            .readValue(
                                    new File(getTablePath()), new TypeReference<List<Item>>() {});

            update(response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Could not read from table file: " + getTablePath(), e);
        }
    }
}
