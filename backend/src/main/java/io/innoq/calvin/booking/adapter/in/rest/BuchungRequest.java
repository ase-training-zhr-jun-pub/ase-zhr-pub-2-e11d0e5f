package io.innoq.calvin.booking.adapter.in.rest;

import org.springframework.lang.Nullable;

public record BuchungRequest(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        @Nullable String notiz) {
}
