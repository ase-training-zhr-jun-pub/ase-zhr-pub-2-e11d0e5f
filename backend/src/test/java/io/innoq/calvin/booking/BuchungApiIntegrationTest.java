package io.innoq.calvin.booking;

import io.innoq.calvin.booking.adapter.out.persistence.InMemoryBuchungRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BuchungApiIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    InMemoryBuchungRepository repository;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        repository.clear();
        // Kein Exception-Wurf bei 4xx/5xx — wir wollen die Status-Codes selbst prüfen
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) {
                return false;
            }
        });
    }

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    // --- 1. Buchung anlegen — Happy Path ---

    @Test
    void buchungAnlegen_gibtBuchungsnummerInKorrektemFormatZurueck() {
        ResponseEntity<Map> response = postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-01-01",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Sprint Planning"
                }
                """
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String buchungsnummer = (String) response.getBody().get("buchungsnummer");
        assertThat(buchungsnummer).matches("BUC-[0-9A-F]{6}");
    }

    // --- 2. Buchung anlegen mit Notiz ---

    @Test
    void buchungAnlegen_mitNotiz_gibt201ZurueckMitBuchungsnummer() {
        ResponseEntity<Map> response = postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-01-02",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Sprint Planning",
                  "notiz": "Bitte Beamer vorbereiten"
                }
                """
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String buchungsnummer = (String) response.getBody().get("buchungsnummer");
        assertThat(buchungsnummer).matches("BUC-[0-9A-F]{6}");
    }

    // --- 3. Buchung erscheint in der Liste ---

    @Test
    void buchungAnlegen_erscheintDanachInDerListe() {
        postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-2",
                  "datum": "2099-02-01",
                  "von": "10:00",
                  "bis": "12:00",
                  "titel": "Retrospektive"
                }
                """
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", basicAuth("alex.berger"));
        ResponseEntity<List> listResponse = restTemplate.exchange(
                url("/api/buchungen"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class
        );

        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> buchungen = listResponse.getBody();
        assertThat(buchungen).hasSize(1);
        Map<String, Object> eintrag = buchungen.get(0);
        assertThat(eintrag.get("raumId")).isEqualTo("koeln-1-2");
        assertThat(eintrag.get("datum")).isEqualTo("2099-02-01");
        assertThat(eintrag.get("von")).isEqualTo("10:00");
        assertThat(eintrag.get("bis")).isEqualTo("12:00");
        assertThat(eintrag.get("titel")).isEqualTo("Retrospektive");
    }

    // --- 4. Vollständiger Flow: Buchen → Verfügbarkeit prüft Belegung ---

    @Test
    void buchungAnlegen_dannVerfuegbarkeitFalse() {
        // Vor der Buchung: Raum ist frei
        ResponseEntity<Map> vorher = restTemplate.getForEntity(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?datum=2099-01-01&von=09:00&bis=11:00"),
                Map.class
        );
        assertThat(vorher.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(vorher.getBody().get("verfuegbar")).isEqualTo(true);

        // Buchung anlegen
        postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-01-01",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Meeting"
                }
                """
        );

        // Nach der Buchung: Raum ist belegt
        ResponseEntity<Map> nachher = restTemplate.getForEntity(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?datum=2099-01-01&von=09:00&bis=11:00"),
                Map.class
        );
        assertThat(nachher.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(nachher.getBody().get("verfuegbar")).isEqualTo(false);
    }

    // --- 5. Verfügbarkeit — überlappende Zeitfenster ---

    @Test
    void verfuegbarkeit_ueberlappenderSlot_istBelegt() {
        // Buchung 09:00–11:00
        postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-03-01",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Basis-Buchung"
                }
                """
        );

        // Anfrage 10:00–12:00 → überlappend → belegt
        ResponseEntity<Map> response = restTemplate.getForEntity(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?datum=2099-03-01&von=10:00&bis=12:00"),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("verfuegbar")).isEqualTo(false);
    }

    @Test
    void verfuegbarkeit_angrenzenderSlotDanach_istFrei() {
        // Buchung 09:00–11:00
        postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-03-02",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Basis-Buchung"
                }
                """
        );

        // Anfrage 11:00–13:00 → angrenzend (kein Overlap) → frei
        ResponseEntity<Map> response = restTemplate.getForEntity(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?datum=2099-03-02&von=11:00&bis=13:00"),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("verfuegbar")).isEqualTo(true);
    }

    @Test
    void verfuegbarkeit_angrenzenderSlotDavor_istFrei() {
        // Buchung 09:00–11:00
        postBuchung(
                "alex.berger",
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-03-03",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Basis-Buchung"
                }
                """
        );

        // Anfrage 08:00–09:00 → angrenzend davor (kein Overlap) → frei
        ResponseEntity<Map> response = restTemplate.getForEntity(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?datum=2099-03-03&von=08:00&bis=09:00"),
                Map.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("verfuegbar")).isEqualTo(true);
    }

    // --- 6. Auth-Fehler ---

    @Test
    void buchungenAbrufen_ohneAuthHeader_gibt400() {
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/buchungen"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void buchungAnlegen_ohneAuthHeader_gibt400() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-01-01",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Test"
                }
                """,
                headers
        );
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/buchungen"),
                HttpMethod.POST,
                request,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void buchungAnlegen_mitLeererNutzerId_gibt400() {
        // Base64 von ":" → NutzerId ist leer
        String emptyUserAuth = "Basic " + Base64.getEncoder().encodeToString(":".getBytes());
        ResponseEntity<String> response = postBuchungRaw(
                emptyUserAuth,
                """
                {
                  "raumId": "koeln-1-1",
                  "datum": "2099-01-01",
                  "von": "09:00",
                  "bis": "11:00",
                  "titel": "Test"
                }
                """
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // --- 7. Verfügbarkeit ohne Parameter ---

    @Test
    void verfuegbarkeit_ohneDatumParameter_gibt400() {
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/raeume/koeln-1-1/verfuegbarkeit?von=09:00&bis=11:00"),
                HttpMethod.GET,
                HttpEntity.EMPTY,
                String.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    // --- Hilfsmethoden ---

    private static String basicAuth(String nutzerId) {
        return "Basic " + Base64.getEncoder().encodeToString((nutzerId + ":").getBytes());
    }

    private ResponseEntity<Map> postBuchung(String nutzerId, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", basicAuth(nutzerId));
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url("/api/buchungen"), HttpMethod.POST, request, Map.class);
    }

    private ResponseEntity<String> postBuchungRaw(String authHeader, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authHeader);
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        return restTemplate.exchange(url("/api/buchungen"), HttpMethod.POST, request, String.class);
    }
}
