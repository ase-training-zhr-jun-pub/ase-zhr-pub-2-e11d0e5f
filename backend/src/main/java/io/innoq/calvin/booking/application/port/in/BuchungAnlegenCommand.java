package io.innoq.calvin.booking.application.port.in;

public record BuchungAnlegenCommand(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId) {
}
