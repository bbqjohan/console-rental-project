package org.example.services;

import org.example.entity.Rental;
import org.example.registry.RentalRegistry;

import java.io.IOException;
import java.util.List;

public final class RentalService {
    public static void addRental(Rental rental) throws IOException {
        RentalRegistry reg = new RentalRegistry();
        reg.read();
        reg.add(rental);
        reg.write();
    }

    public static void updateRental(Rental rental) throws IOException {
        RentalRegistry reg = new RentalRegistry();
        reg.read();
        reg.set(rental);
        reg.write();
    }

    public static List<Rental> readAsList() throws IOException {
        RentalRegistry reg = new RentalRegistry();
        reg.read();

        return reg.getAll();
    }
}
