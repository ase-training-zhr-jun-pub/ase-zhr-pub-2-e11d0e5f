package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.BuchungAnlegenCommand;
import io.innoq.calvin.booking.application.port.in.BuchungAnlegenUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuchungController.class)
class BuchungControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    BuchungAnlegenUseCase buchungAnlegenUseCase;

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

    private static String basicAuth(String nutzerId) {
        return "Basic " + Base64.getEncoder().encodeToString((nutzerId + ":").getBytes());
    }
}
