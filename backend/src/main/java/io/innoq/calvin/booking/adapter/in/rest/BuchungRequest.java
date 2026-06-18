package io.innoq.calvin.booking.adapter.in.rest;

import jakarta.validation.constraints.Size;
import org.springframework.lang.Nullable;

public record BuchungRequest(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        @Nullable @Size(max = 500) String notiz) {
}
