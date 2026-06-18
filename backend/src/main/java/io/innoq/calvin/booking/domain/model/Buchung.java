package io.innoq.calvin.booking.domain.model;

import org.springframework.lang.Nullable;

public record Buchung(
        String buchungsnummer,
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId,
        @Nullable String notiz) {
}
