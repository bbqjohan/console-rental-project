package org.example.entity.price;

import java.time.LocalDate;

public interface PricePolicy {
    double DAILY_RATE = 100;

    double calculatePrice(LocalDate start, LocalDate stop);
}
