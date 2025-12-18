package org.example.registry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.entity.Rental;
import org.example.registry.table.EntityTable;

import java.io.File;
import java.io.IOException;
import java.util.List;

public final class RentalRegistry extends EntityTable<Rental> {
    public RentalRegistry() {
        super("rentals.json");
    }

    @Override
    public void read() throws IOException {
        try {
            List<Rental> response =
                    new ObjectMapper()
                            .readValue(
                                    new File(getTablePath()), new TypeReference<List<Rental>>() {});

            update(response);
        } catch (IOException e) {
            throw new IOException("Could not read from table file: " + getTablePath(), e);
        }
    }
}
