package io.innoq.calvin.booking.adapter.in.rest;

public record BuchungRequest(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel) {
}
