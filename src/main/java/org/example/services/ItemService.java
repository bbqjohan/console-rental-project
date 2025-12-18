package org.example.services;

import org.example.entity.item.Item;
import org.example.registry.ItemRegistry;

import java.io.IOException;
import java.util.List;

public final class ItemService {
    public static void addNewItem(Item item) throws IOException {
        ItemRegistry reg = new ItemRegistry();
        reg.read();
        reg.add(item);
        reg.write();
    }

    public static void updateItem(Item item) throws IOException {
        ItemRegistry reg = new ItemRegistry();
        reg.read();
        reg.set(item);
        reg.write();
    }

    public static void removeItem(Item item) throws IOException {
        ItemRegistry reg = new ItemRegistry();
        reg.read();
        reg.remove(item.getId());
        reg.write();
    }

    public static List<Item> readAsList() throws IOException {
        ItemRegistry reg = new ItemRegistry();
        reg.read();

        return reg.getAll();
    }

    public static Item getItem(long id) throws IOException {
        ItemRegistry reg = new ItemRegistry();
        reg.read();

        return reg.get(id);
    }
}
