package io.innoq.calvin.booking.application;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.out.BuchungRepository;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BuchungServiceTest {

    private final BuchungRepository repository = mock(BuchungRepository.class);
    private final BuchungService service = new BuchungService(repository);

    private static BuchungAnlegenCommand command(String notiz) {
        return new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", "09:00", "11:00",
                "Sprint Planning", notiz, "alex.berger");
    }

    @Test
    void buchen_gibtBuchungsnummerImErwartetenFormatZurueck() {
        String buchungsnummer = service.buchen(command("Beamer benoetigt"));

        assertThat(buchungsnummer).matches("BUC-[0-9A-F]{6}");
    }

    @Test
    void buchen_speichertBuchungMitFeldernAusCommand() {
        BuchungAnlegenCommand command = command("Beamer benoetigt");

        String buchungsnummer = service.buchen(command);

        ArgumentCaptor<Buchung> captor = ArgumentCaptor.forClass(Buchung.class);
        verify(repository, times(1)).speichern(captor.capture());

        Buchung gespeichert = captor.getValue();
        assertThat(gespeichert.buchungsnummer()).isEqualTo(buchungsnummer);
        assertThat(gespeichert.raumId()).isEqualTo(command.raumId());
        assertThat(gespeichert.datum()).isEqualTo(command.datum());
        assertThat(gespeichert.von()).isEqualTo(command.von());
        assertThat(gespeichert.bis()).isEqualTo(command.bis());
        assertThat(gespeichert.titel()).isEqualTo(command.titel());
        assertThat(gespeichert.nutzerId()).isEqualTo(command.nutzerId());
        assertThat(gespeichert.notiz()).isEqualTo(command.notiz());
    }

    @Test
    void buchen_reichtNotizGleichNullDurch() {
        service.buchen(command(null));

        ArgumentCaptor<Buchung> captor = ArgumentCaptor.forClass(Buchung.class);
        verify(repository).speichern(captor.capture());
        assertThat(captor.getValue().notiz()).isNull();
    }

    @Test
    void buchen_zweiAufrufeErzeugenUnterschiedlicheBuchungsnummern() {
        String erste = service.buchen(command(null));
        String zweite = service.buchen(command(null));

        assertThat(erste).isNotEqualTo(zweite);
    }

    @Test
    void alleAbrufen_delegiertAnRepositoryUndGibtDessenErgebnisZurueck() {
        List<Buchung> erwartet = List.of(
                new Buchung("BUC-ABC123", "koeln-1-1", "2026-06-17",
                        "09:00", "11:00", "Sprint Planning", "alex.berger", null));
        given(repository.alleAbrufen()).willReturn(erwartet);

        List<Buchung> ergebnis = service.alleAbrufen();

        assertThat(ergebnis).isSameAs(erwartet);
    }
}
