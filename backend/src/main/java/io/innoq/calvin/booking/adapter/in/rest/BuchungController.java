package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import io.innoq.calvin.booking.application.port.in.BuchungenAbrufenUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api/buchungen")
public class BuchungController {

    private final BuchungAnlegenUseCase buchungAnlegenUseCase;
    private final BuchungenAbrufenUseCase buchungenAbrufenUseCase;

    public BuchungController(BuchungAnlegenUseCase buchungAnlegenUseCase,
                             BuchungenAbrufenUseCase buchungenAbrufenUseCase) {
        this.buchungAnlegenUseCase = buchungAnlegenUseCase;
        this.buchungenAbrufenUseCase = buchungenAbrufenUseCase;
    }

    @GetMapping
    public List<BuchungListeEintrag> buchungenAbrufen(
            @RequestHeader("Authorization") String authHeader) {
        String nutzerId = extractNutzerId(authHeader);
        return buchungenAbrufenUseCase.abrufenFuerNutzer(nutzerId).stream()
                .map(BuchungListeEintrag::from)
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuchungResponse buchen(
            @Valid @RequestBody BuchungRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String nutzerId = extractNutzerId(authHeader);
        var command = new BuchungAnlegenCommand(
                request.raumId(), request.datum(), request.von(),
                request.bis(), request.titel(), request.notiz(), nutzerId);

        return new BuchungResponse(buchungAnlegenUseCase.buchen(command));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String ungueltigeBuchung(IllegalArgumentException e) {
        return e.getMessage();
    }

    private String extractNutzerId(String authHeader) {
        // Basic-Auth ohne Passwort: "Basic <base64(nutzerId:)>"
        try {
            String decoded = new String(Base64.getDecoder().decode(
                    authHeader.substring("Basic ".length())));
            String nutzerId = decoded.split(":", 2)[0];
            if (nutzerId.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NutzerId fehlt im Authorization-Header");
            }
            return nutzerId;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ungültiger Authorization-Header");
        }
    }
}
