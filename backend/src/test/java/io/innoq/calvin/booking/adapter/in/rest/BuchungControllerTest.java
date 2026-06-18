package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import io.innoq.calvin.booking.application.port.in.BuchungenAbrufenUseCase;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuchungController.class)
class BuchungControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BuchungAnlegenUseCase buchungAnlegenUseCase;

    @MockitoBean
    BuchungenAbrufenUseCase buchungenAbrufenUseCase;

    @Test
    void buchungAnlegen_gibtBuchungsnummerZurueck() throws Exception {
        given(buchungAnlegenUseCase.buchen(any(BuchungAnlegenCommand.class)))
                .willReturn("BUC-001");

        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buchungsnummer").value("BUC-001"));
    }

    @Test
    void buchungenAbrufen_gibtListeZurueck() throws Exception {
        given(buchungenAbrufenUseCase.abrufenFuerNutzer("alex.berger")).willReturn(List.of(
                new Buchung("BUC-001", "koeln-1-1", "2026-06-17", "09:00", "11:00", "Sprint Planning", "alex.berger", null)
        ));

        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", basicAuth("alex.berger")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buchungsnummer").value("BUC-001"))
                .andExpect(jsonPath("$[0].titel").value("Sprint Planning"));
    }

    @Test
    void buchungenAbrufen_gibtNurBuchungenDesNutzersZurueck() throws Exception {
        given(buchungenAbrufenUseCase.abrufenFuerNutzer("alex.berger")).willReturn(List.of(
                new Buchung("BUC-001", "koeln-1-1", "2026-06-17", "09:00", "11:00", "Sprint Planning", "alex.berger", null)
        ));
        given(buchungenAbrufenUseCase.abrufenFuerNutzer("andere.person")).willReturn(List.of());

        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", basicAuth("andere.person")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void buchungenAbrufen_ohneAuthHeader_gibt400() throws Exception {
        mockMvc.perform(get("/api/buchungen"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitNotiz_speichertNotiz() throws Exception {
        given(buchungAnlegenUseCase.buchen(any(BuchungAnlegenCommand.class)))
                .willReturn("BUC-002");

        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning",
                                  "notiz": "Bitte Beamer vorbereiten"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.buchungsnummer").value("BUC-002"));

        ArgumentCaptor<BuchungAnlegenCommand> captor = ArgumentCaptor.forClass(BuchungAnlegenCommand.class);
        verify(buchungAnlegenUseCase).buchen(captor.capture());
        assertThat(captor.getValue().notiz()).isEqualTo("Bitte Beamer vorbereiten");
    }

    @Test
    void buchungAnlegen_setztNutzerIdAusBasicAuthHeader() throws Exception {
        given(buchungAnlegenUseCase.buchen(any(BuchungAnlegenCommand.class)))
                .willReturn("BUC-003");

        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isCreated());

        ArgumentCaptor<BuchungAnlegenCommand> captor = ArgumentCaptor.forClass(BuchungAnlegenCommand.class);
        verify(buchungAnlegenUseCase).buchen(captor.capture());
        assertThat(captor.getValue().nutzerId()).isEqualTo("alex.berger");
    }

    @Test
    void buchungenAbrufen_mitNichtBasicAuthHeader_gibt400() throws Exception {
        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", "Basic @@@@"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungenAbrufen_mitUngueltigemBase64_gibt400() throws Exception {
        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", "Basic !!!nicht-base64!!!"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungenAbrufen_mitLeererNutzerId_gibt400() throws Exception {
        // Base64 von ":" -> NutzerId ist leer
        String header = "Basic " + Base64.getEncoder().encodeToString(":".getBytes());

        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", header))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitNichtBasicAuthHeader_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Basic @@@@")
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_ohneAuthHeader_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitLeeremTitel_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitBisVorVon_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "11:00",
                                  "bis": "09:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitBisGleichVon_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "09:00",
                                  "bis": "09:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitUngueltigemZeitformat_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "2026-06-17",
                                  "von": "0900",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buchungAnlegen_mitUngueltigemDatumsformat_gibt400() throws Exception {
        mockMvc.perform(post("/api/buchungen")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", basicAuth("alex.berger"))
                        .content("""
                                {
                                  "raumId": "koeln-1-1",
                                  "datum": "17.06.2026",
                                  "von": "09:00",
                                  "bis": "11:00",
                                  "titel": "Sprint Planning"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    private static String basicAuth(String nutzerId) {
        return "Basic " + Base64.getEncoder().encodeToString((nutzerId + ":").getBytes());
    }
}
