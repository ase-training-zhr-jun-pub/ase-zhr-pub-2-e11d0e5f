package io.innoq.calvin.booking.application.port.in;

import io.innoq.calvin.booking.domain.model.Buchung;

import java.util.List;

public interface BuchungenAbrufenUseCase {

    List<Buchung> abrufenFuerNutzer(String nutzerId);
}
