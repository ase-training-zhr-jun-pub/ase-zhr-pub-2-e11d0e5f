package io.innoq.calvin.booking.application.port.out;

import io.innoq.calvin.booking.domain.model.Buchung;

import java.util.List;

public interface BuchungRepository {

    void speichern(Buchung buchung);

    List<Buchung> alleAbrufen();

    List<Buchung> abrufenFuerNutzer(String nutzerId);

    List<Buchung> findeNachRaumUndDatum(String raumId, String datum);
}
