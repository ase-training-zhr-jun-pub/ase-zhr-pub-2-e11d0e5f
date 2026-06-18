package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.domain.model.Buchung;

public record BuchungListeEintrag(
        String buchungsnummer,
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        String nutzerId) {

    static BuchungListeEintrag from(Buchung b) {
        return new BuchungListeEintrag(
                b.buchungsnummer(), b.raumId(), b.datum(),
                b.von(), b.bis(), b.titel(), b.nutzerId());
    }
}
