package org.example.entity.price;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class GamerPrice implements PricePolicy {
    @Override
    public double calculatePrice(LocalDate start, LocalDate stop) {
        long days = ChronoUnit.DAYS.between(start, stop);

        return DAILY_RATE * days * 0.75;
    }
}
