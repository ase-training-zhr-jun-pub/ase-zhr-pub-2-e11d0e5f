package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import io.innoq.calvin.booking.application.port.in.BuchungenAbrufenUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
    public List<BuchungListeEintrag> buchungenAbrufen() {
        return buchungenAbrufenUseCase.alleAbrufen().stream()
                .map(b -> new BuchungListeEintrag(
                        b.buchungsnummer(), b.raumId(), b.datum(),
                        b.von(), b.bis(), b.titel(), b.nutzerId()))
                .toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BuchungResponse buchen(
            @RequestBody BuchungRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String nutzerId = extractNutzerId(authHeader);
        var command = new BuchungAnlegenCommand(
                request.raumId(), request.datum(), request.von(),
                request.bis(), request.titel(), nutzerId);

        return new BuchungResponse(buchungAnlegenUseCase.buchen(command));
    }

    private String extractNutzerId(String authHeader) {
        // Basic-Auth ohne Passwort: "Basic <base64(nutzerId:)>"
        String decoded = new String(Base64.getDecoder().decode(
                authHeader.replace("Basic ", "")));
        return decoded.replace(":", "");
    }
}
