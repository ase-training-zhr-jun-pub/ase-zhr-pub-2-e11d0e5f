package io.innoq.calvin.booking.adapter.in.rest;

public record BuchungListeEintrag(
        String buchungsnummer,
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId) {
}
