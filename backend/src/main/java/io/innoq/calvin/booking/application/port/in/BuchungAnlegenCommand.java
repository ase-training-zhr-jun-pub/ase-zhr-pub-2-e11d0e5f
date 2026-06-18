package io.innoq.calvin.booking.application.port.in;

import org.springframework.lang.Nullable;

public record BuchungAnlegenCommand(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId,
        @Nullable String notiz) {
}
