package io.innoq.calvin.booking.application;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import io.innoq.calvin.booking.application.port.in.BuchungenAbrufenUseCase;
import io.innoq.calvin.booking.application.port.out.BuchungRepository;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BuchungService implements BuchungAnlegenUseCase, BuchungenAbrufenUseCase {

    private final BuchungRepository buchungRepository;

    public BuchungService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    @Override
    public String buchen(BuchungAnlegenCommand command) {
        String buchungsnummer = "BUC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        buchungRepository.speichern(new Buchung(
                buchungsnummer,
                command.raumId(),
                command.datum(),
                command.von(),
                command.bis(),
                command.titel(),
                command.nutzerId(),
                command.notiz()));
        return buchungsnummer;
    }

    @Override
    public List<Buchung> alleAbrufen() {
        return buchungRepository.alleAbrufen();
    }
}
