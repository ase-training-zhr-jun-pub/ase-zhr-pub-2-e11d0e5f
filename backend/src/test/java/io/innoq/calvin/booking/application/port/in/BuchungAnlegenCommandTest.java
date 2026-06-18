package io.innoq.calvin.booking.application.port.in;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuchungAnlegenCommandTest {

    private static BuchungAnlegenCommand command(String raumId, String datum, String von,
                                                 String bis, String titel, String notiz) {
        return new BuchungAnlegenCommand(raumId, datum, von, bis, titel, notiz, "alex.berger");
    }

    private static BuchungAnlegenCommand gueltig(String raumId, String datum, String von,
                                                 String bis, String titel, String notiz) {
        // Hilfsmethode mit gültigen Defaults; pro Test wird genau ein Feld variiert.
        return command(
                raumId == null ? "koeln-1-1" : raumId,
                datum == null ? "2026-06-17" : datum,
                von == null ? "09:00" : von,
                bis == null ? "11:00" : bis,
                titel == null ? "Sprint Planning" : titel,
                notiz);
    }

    @Test
    void gueltigesCommand_ohneNotiz_wirdErstellt() {
        assertThatCode(() -> new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", "09:00", "11:00", "Sprint Planning", null, "alex.berger"))
                .doesNotThrowAnyException();
    }

    @Test
    void gueltigesCommand_mitNotiz_wirdErstellt() {
        assertThatCode(() -> new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", "09:00", "11:00", "Sprint Planning", "Beamer", "alex.berger"))
                .doesNotThrowAnyException();
    }

    @Test
    void leereRaumId_wirftException() {
        assertThatThrownBy(() -> gueltig("   ", null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullRaumId_wirftException() {
        assertThatThrownBy(() -> new BuchungAnlegenCommand(
                null, "2026-06-17", "09:00", "11:00", "Sprint Planning", null, "alex.berger"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leeresDatum_wirftException() {
        assertThatThrownBy(() -> gueltig(null, "   ", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullDatum_wirftException() {
        assertThatThrownBy(() -> new BuchungAnlegenCommand(
                "koeln-1-1", null, "09:00", "11:00", "Sprint Planning", null, "alex.berger"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leeresTitel_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, null, null, "   ", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullTitel_wirftException() {
        assertThatThrownBy(() -> new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", "09:00", "11:00", null, null, "alex.berger"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leeresVon_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "   ", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullVon_wirftException() {
        assertThatThrownBy(() -> new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", null, "11:00", "Sprint Planning", null, "alex.berger"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void leeresBis_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, null, "   ", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void nullBis_wirftException() {
        assertThatThrownBy(() -> new BuchungAnlegenCommand(
                "koeln-1-1", "2026-06-17", "09:00", null, "Sprint Planning", null, "alex.berger"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void datumMitUngueltigemWert_wirftException() {
        assertThatThrownBy(() -> gueltig(null, "2026-13-40", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void datumImDeutschenFormat_wirftException() {
        assertThatThrownBy(() -> gueltig(null, "17.06.2026", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void datumOhneFuehrendeNullen_wirftException() {
        assertThatThrownBy(() -> gueltig(null, "2026-6-7", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void vonNurStunde_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "9", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void vonOhneDoppelpunkt_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "0900", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void vonMitUngueltigerStunde_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "25:00", "23:00", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void vonMitEinstelligenMinuten_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "9:5", null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bisGleichVon_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "09:00", "09:00", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bisVorVon_wirftException() {
        assertThatThrownBy(() -> gueltig(null, null, "11:00", "09:00", null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void bisEineMinuteNachVon_wirdErstellt() {
        assertThatCode(() -> gueltig(null, null, "09:00", "09:01", null, null))
                .doesNotThrowAnyException();
    }
}
