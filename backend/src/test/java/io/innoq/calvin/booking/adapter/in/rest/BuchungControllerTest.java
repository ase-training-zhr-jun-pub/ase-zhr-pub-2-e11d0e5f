package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import io.innoq.calvin.booking.application.port.in.BuchungenAbrufenUseCase;
import io.innoq.calvin.booking.domain.model.Buchung;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
        given(buchungenAbrufenUseCase.alleAbrufen()).willReturn(List.of(
                new Buchung("BUC-001", "koeln-1-1", "2026-06-17", "09:00", "11:00", "Sprint Planning", "alex.berger", null)
        ));

        mockMvc.perform(get("/api/buchungen")
                        .header("Authorization", basicAuth("alex.berger")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].buchungsnummer").value("BUC-001"))
                .andExpect(jsonPath("$[0].titel").value("Sprint Planning"));
    }

    @Test
    void buchungenAbrufen_ohneAuthHeader_gibt400() throws Exception {
        mockMvc.perform(get("/api/buchungen"))
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

    private static String basicAuth(String nutzerId) {
        return "Basic " + Base64.getEncoder().encodeToString((nutzerId + ":").getBytes());
    }
}
