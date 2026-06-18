package io.innoq.calvin.booking.adapter.out.persistence;

import io.innoq.calvin.booking.application.port.out.BuchungRepository;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class InMemoryBuchungRepository implements BuchungRepository {

    private final List<Buchung> buchungen = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void speichern(Buchung buchung) {
        buchungen.add(buchung);
    }

    @Override
    public List<Buchung> alleAbrufen() {
        return List.copyOf(buchungen);
    }

    @Override
    public List<Buchung> abrufenFuerNutzer(String nutzerId) {
        return buchungen.stream()
                .filter(b -> nutzerId.equals(b.nutzerId()))
                .toList();
    }
}
