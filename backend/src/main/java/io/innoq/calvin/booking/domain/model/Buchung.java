package io.innoq.calvin.booking.domain.model;

public record Buchung(
        String buchungsnummer,
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId,
        String notiz) {
}
