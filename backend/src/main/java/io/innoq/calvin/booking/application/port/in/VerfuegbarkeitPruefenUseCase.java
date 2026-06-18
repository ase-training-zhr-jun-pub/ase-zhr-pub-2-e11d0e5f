package io.innoq.calvin.booking.application.port.in;

public interface VerfuegbarkeitPruefenUseCase {

    boolean istVerfuegbar(String raumId, String datum, String von, String bis);
}
