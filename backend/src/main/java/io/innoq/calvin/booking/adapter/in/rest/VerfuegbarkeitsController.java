package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.VerfuegbarkeitPruefenUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/raeume")
public class VerfuegbarkeitsController {

    private final VerfuegbarkeitPruefenUseCase verfuegbarkeitPruefenUseCase;

    public VerfuegbarkeitsController(VerfuegbarkeitPruefenUseCase verfuegbarkeitPruefenUseCase) {
        this.verfuegbarkeitPruefenUseCase = verfuegbarkeitPruefenUseCase;
    }

    @GetMapping("/{raumId}/verfuegbarkeit")
    public VerfuegbarkeitsResponse verfuegbarkeitPruefen(
            @PathVariable String raumId,
            @RequestParam(required = true) String datum,
            @RequestParam(required = true) String von,
            @RequestParam(required = true) String bis) {
        if (datum == null || datum.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'datum' darf nicht leer sein");
        }
        if (von == null || von.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'von' darf nicht leer sein");
        }
        if (bis == null || bis.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter 'bis' darf nicht leer sein");
        }
        boolean verfuegbar = verfuegbarkeitPruefenUseCase.istVerfuegbar(raumId, datum, von, bis);
        return new VerfuegbarkeitsResponse(verfuegbar);
    }
}
