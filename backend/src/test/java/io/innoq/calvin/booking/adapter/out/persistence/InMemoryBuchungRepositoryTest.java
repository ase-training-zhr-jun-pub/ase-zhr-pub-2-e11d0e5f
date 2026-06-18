package io.innoq.calvin.booking.adapter.out.persistence;

import io.innoq.calvin.booking.domain.model.Buchung;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryBuchungRepositoryTest {

    private static Buchung buchung(String nummer) {
        return new Buchung(nummer, "koeln-1-1", "2026-06-17", "09:00", "11:00",
                "Sprint Planning", "alex.berger", null);
    }

    @Test
    void neuesRepository_liefertLeereListe() {
        InMemoryBuchungRepository repository = new InMemoryBuchungRepository();

        assertThat(repository.alleAbrufen()).isEmpty();
    }

    @Test
    void nachSpeichern_liefertBuchungenInEinfuegereihenfolge() {
        InMemoryBuchungRepository repository = new InMemoryBuchungRepository();
        Buchung erste = buchung("BUC-001");
        Buchung zweite = buchung("BUC-002");

        repository.speichern(erste);
        repository.speichern(zweite);

        assertThat(repository.alleAbrufen()).containsExactly(erste, zweite);
    }

    @Test
    void alleAbrufen_liefertUnveraenderlicheListe() {
        InMemoryBuchungRepository repository = new InMemoryBuchungRepository();
        repository.speichern(buchung("BUC-001"));

        List<Buchung> liste = repository.alleAbrufen();

        assertThatThrownBy(() -> liste.add(buchung("BUC-002")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void alleAbrufen_liefertKopieDieSpaetereAenderungenNichtSieht() {
        InMemoryBuchungRepository repository = new InMemoryBuchungRepository();
        repository.speichern(buchung("BUC-001"));

        List<Buchung> vorher = repository.alleAbrufen();
        repository.speichern(buchung("BUC-002"));

        assertThat(vorher).hasSize(1);
        assertThat(repository.alleAbrufen()).hasSize(2);
    }
}
