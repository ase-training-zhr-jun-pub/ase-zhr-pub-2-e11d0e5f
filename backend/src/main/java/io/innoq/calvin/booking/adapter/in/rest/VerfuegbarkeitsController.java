package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.VerfuegbarkeitPruefenUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestParam String datum,
            @RequestParam String von,
            @RequestParam String bis) {
        boolean verfuegbar = verfuegbarkeitPruefenUseCase.istVerfuegbar(raumId, datum, von, bis);
        return new VerfuegbarkeitsResponse(verfuegbar);
    }
}
