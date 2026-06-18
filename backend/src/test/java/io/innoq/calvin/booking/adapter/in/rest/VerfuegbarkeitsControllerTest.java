package io.innoq.calvin.booking.adapter.in.rest;

import io.innoq.calvin.booking.application.port.in.VerfuegbarkeitPruefenUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VerfuegbarkeitsController.class)
class VerfuegbarkeitsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    VerfuegbarkeitPruefenUseCase verfuegbarkeitPruefenUseCase;

    @Test
    void verfuegbarkeitPruefen_raumIstFrei_gibtVerfuegbarZurueck() throws Exception {
        given(verfuegbarkeitPruefenUseCase.istVerfuegbar("koeln-1-1", "2026-06-17", "09:00", "11:00"))
                .willReturn(true);

        mockMvc.perform(get("/api/raeume/koeln-1-1/verfuegbarkeit")
                        .param("datum", "2026-06-17")
                        .param("von", "09:00")
                        .param("bis", "11:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verfuegbar").value(true));
    }

    @Test
    void verfuegbarkeitPruefen_raumIstBelegt_gibtNichtVerfuegbarZurueck() throws Exception {
        given(verfuegbarkeitPruefenUseCase.istVerfuegbar("koeln-1-1", "2026-06-17", "13:00", "14:30"))
                .willReturn(false);

        mockMvc.perform(get("/api/raeume/koeln-1-1/verfuegbarkeit")
                        .param("datum", "2026-06-17")
                        .param("von", "13:00")
                        .param("bis", "14:30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verfuegbar").value(false));
    }
}
