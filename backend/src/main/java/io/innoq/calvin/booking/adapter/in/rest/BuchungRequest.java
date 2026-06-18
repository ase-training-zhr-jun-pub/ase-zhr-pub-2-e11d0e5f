package io.innoq.calvin.booking.adapter.in.rest;

import jakarta.validation.constraints.Size;

public record BuchungRequest(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        @Size(max = 500) String notiz) {
}
