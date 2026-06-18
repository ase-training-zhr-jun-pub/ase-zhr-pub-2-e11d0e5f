package io.innoq.calvin.booking.application;

import io.innoq.calvin.booking.application.port.out.BuchungRepository;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class VerfuegbarkeitsServiceTest {

    @Mock
    BuchungRepository buchungRepository;

    @InjectMocks
    VerfuegbarkeitsService verfuegbarkeitsService;

    private static Buchung buchung(String von, String bis) {
        return new Buchung("BUC-001", "koeln-1-1", "2026-06-17", von, bis, "Titel", "user", null);
    }

    @Test
    void istVerfuegbar_keineBuchungenVorhanden_gibtTrueZurueck() {
        given(buchungRepository.findeNachRaumUndDatum("koeln-1-1", "2026-06-17"))
                .willReturn(List.of());

        assertThat(verfuegbarkeitsService.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00")).isTrue();
    }

    @Test
    void istVerfuegbar_zeitraumUeberlapptBestehendeBuchung_gibtFalseZurueck() {
        given(buchungRepository.findeNachRaumUndDatum("koeln-1-1", "2026-06-17"))
                .willReturn(List.of(buchung("10:00", "12:00")));

        assertThat(verfuegbarkeitsService.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00")).isFalse();
    }

    @Test
    void istVerfuegbar_zeitraumLiegtVorBestehenderBuchung_gibtTrueZurueck() {
        given(buchungRepository.findeNachRaumUndDatum("koeln-1-1", "2026-06-17"))
                .willReturn(List.of(buchung("12:00", "14:00")));

        assertThat(verfuegbarkeitsService.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00")).isTrue();
    }

    @Test
    void istVerfuegbar_zeitraumLiegtNachBestehenderBuchung_gibtTrueZurueck() {
        given(buchungRepository.findeNachRaumUndDatum("koeln-1-1", "2026-06-17"))
                .willReturn(List.of(buchung("08:00", "09:00")));

        assertThat(verfuegbarkeitsService.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00")).isTrue();
    }

    @Test
    void istVerfuegbar_zeitraumGrenztGenauAnBestehendeBuchungAn_gibtTrueZurueck() {
        given(buchungRepository.findeNachRaumUndDatum("koeln-1-1", "2026-06-17"))
                .willReturn(List.of(buchung("11:00", "13:00")));

        assertThat(verfuegbarkeitsService.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00")).isTrue();
    }
}
