package io.innoq.calvin.booking.application.port.in;

import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public record BuchungAnlegenCommand(
        String raumId,
        String datum,
        String von,
        String bis,
        String titel,
        @Nullable String notiz,
        String nutzerId) {

    private static final DateTimeFormatter ZEIT_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public BuchungAnlegenCommand {
        if (raumId == null || raumId.isBlank()) {
            throw new IllegalArgumentException("raumId darf nicht leer sein");
        }
        if (datum == null || datum.isBlank()) {
            throw new IllegalArgumentException("datum darf nicht leer sein");
        }
        if (von == null || von.isBlank()) {
            throw new IllegalArgumentException("von darf nicht leer sein");
        }
        if (bis == null || bis.isBlank()) {
            throw new IllegalArgumentException("bis darf nicht leer sein");
        }
        if (titel == null || titel.isBlank()) {
            throw new IllegalArgumentException("titel darf nicht leer sein");
        }

        try {
            LocalDate.parse(datum);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("datum muss im Format yyyy-MM-dd vorliegen");
        }

        LocalTime vonZeit;
        LocalTime bisZeit;
        try {
            vonZeit = LocalTime.parse(von, ZEIT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("von muss eine gültige Uhrzeit im Format HH:mm sein");
        }
        try {
            bisZeit = LocalTime.parse(bis, ZEIT_FORMAT);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("bis muss eine gültige Uhrzeit im Format HH:mm sein");
        }

        if (!bisZeit.isAfter(vonZeit)) {
            throw new IllegalArgumentException("Die Endzeit muss nach der Startzeit liegen");
        }
    }
}
