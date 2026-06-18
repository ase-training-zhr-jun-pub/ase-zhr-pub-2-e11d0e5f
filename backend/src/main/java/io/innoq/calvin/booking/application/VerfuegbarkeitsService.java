package io.innoq.calvin.booking.application;

import io.innoq.calvin.booking.application.port.in.VerfuegbarkeitPruefenUseCase;
import io.innoq.calvin.booking.application.port.out.BuchungRepository;
import org.springframework.stereotype.Service;

@Service
public class VerfuegbarkeitsService implements VerfuegbarkeitPruefenUseCase {

    private final BuchungRepository buchungRepository;

    public VerfuegbarkeitsService(BuchungRepository buchungRepository) {
        this.buchungRepository = buchungRepository;
    }

    @Override
    public boolean istVerfuegbar(String raumId, String datum, String von, String bis) {
        return buchungRepository.findeNachRaumUndDatum(raumId, datum).stream()
                .noneMatch(b -> von.compareTo(b.bis()) < 0 && b.von().compareTo(bis) < 0);
    }
}
