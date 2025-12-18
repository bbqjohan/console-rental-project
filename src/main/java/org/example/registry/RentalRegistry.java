package org.example.registry;

import com.fasterxml.jackson.core.type.TypeReference;

import org.example.entity.Rental;
import org.example.registry.table.EntityTable;

import java.io.IOException;
import java.util.List;

public final class RentalRegistry extends EntityTable<Rental> {
    public RentalRegistry() {
        super("rentals.json");
    }

    @Override
    public void read() throws IOException {
        update(read(new TypeReference<List<Rental>>() {}));
    }
}
