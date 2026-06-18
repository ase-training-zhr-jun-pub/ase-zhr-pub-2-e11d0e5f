package io.innoq.calvin.booking.application.port.in;

import org.springframework.lang.Nullable;

public record BuchungAnlegenCommand(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        @Nullable String notiz,
        String nutzerId) {
}
