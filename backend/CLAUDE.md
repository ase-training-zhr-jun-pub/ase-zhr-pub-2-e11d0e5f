# Booking Service — Backend

## Dokumentation

| Dokument | Pfad |
|---|---|
| Arc42 Architekturdokumentation | [`docs/arc42/arc42.md`](../docs/arc42/arc42.md) |
| ADR-001: Frontend + Booking Service | [`docs/arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md`](../docs/arc42/adrs/ADR-001-frontend-prototyp-und-booking-service.md) |
| ADR-002: Mock-Daten statt Ressource-Service | [`docs/arc42/adrs/ADR-002-ressource-service-in-spa-als-mock-daten.md`](../docs/arc42/adrs/ADR-002-ressource-service-in-spa-als-mock-daten.md) |
| ADR-003: Basic-Auth ohne Passwörter | [`docs/arc42/adrs/ADR-003-basic-auth-statt-okta-fuer-prototyp.md`](../docs/arc42/adrs/ADR-003-basic-auth-statt-okta-fuer-prototyp.md) |
| Technische Schulden | [`docs/architektur/technische-schulden.md`](../docs/architektur/technische-schulden.md) |
| Qualitätsanforderungen | [`docs/architektur/qualitätsanforderungen.md`](../docs/architektur/qualitätsanforderungen.md) |

---

## Technologie-Stack

| Komponente | Technologie |
|---|---|
| Sprache | Java 21 |
| Framework | Spring Boot 4.1 (WebMVC) |
| Build-Tool | Maven (Wrapper: `./mvnw`) |
| Datenbank | H2 In-Memory (Prototyp) |
| Authentifizierung | Basic-Auth ohne Passwörter (Prototyp, siehe ADR-003) |
| Port | 8081 |

---

## Ordner-Struktur

```
backend/
├── src/
│   ├── main/
│   │   ├── java/io/innoq/calvin/booking/
│   │   │   ├── adapter/
│   │   │   │   ├── in/
│   │   │   │   │   └── rest/          # REST-Controller (eingehende Adapter)
│   │   │   │   └── out/
│   │   │   │       └── persistence/   # Repository-Implementierungen (ausgehende Adapter)
│   │   │   ├── application/
│   │   │   │   └── port/
│   │   │   │       ├── in/            # Eingehende Ports (Use-Case-Interfaces)
│   │   │   │       └── out/           # Ausgehende Ports (Repository-Interfaces)
│   │   │   ├── domain/
│   │   │   │   ├── model/             # Entitäten, Value Objects (Buchung, Zeitraum …)
│   │   │   │   └── service/           # Domain-Services (Konfliktprüfung …)
│   │   │   └── BookingServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/io/innoq/calvin/booking/
│           ├── adapter/in/rest/       # Controller-Tests (MockMvc)
│           └── domain/                # Unit-Tests für Domain-Logik
├── pom.xml
└── mvnw
```

---

## Backend-Architektur: Hexagonal (Ports & Adapters)

Die Domain-Logik ist vollständig isoliert von Framework und Infrastruktur.

```
┌─────────────────────────────────────────────────────┐
│                     DOMAIN                          │
│   Buchung · Zeitraum · Konfliktprüfung              │
│   Kein Spring, kein JPA, pure Java                  │
└──────────────┬──────────────────────┬───────────────┘
               │ Port (in)            │ Port (out)
               ▼                      ▼
  ┌────────────────────┐   ┌────────────────────────┐
  │  Application       │   │  Application           │
  │  (Use Cases)       │   │  (Repository-Interface)│
  └────────┬───────────┘   └───────────┬────────────┘
           │                           │
           ▼                           ▼
  ┌────────────────────┐   ┌────────────────────────┐
  │  Adapter IN        │   │  Adapter OUT           │
  │  REST-Controller   │   │  H2-Repository         │
  │  (Spring WebMVC)   │   │  (Spring Data JPA)     │
  └────────────────────┘   └────────────────────────┘
```

**Regel**: Der Domain-Kern importiert **kein** Spring, **kein** JPA und **keine** Adapter-Klassen.

---

## Wichtige Dateien

| Datei | Zweck |
|---|---|
| `src/main/resources/application.properties` | Port (8081), Datenbank-Config |
| `pom.xml` | Dependencies, Java-Version, Build-Config |
| `BookingServiceApplication.java` | Spring-Boot-Einstiegspunkt |
| `HelloController.java` | Smoke-Test-Endpunkt `/api/hello` — kann später entfernt werden |

---

## Bash-Commands

```bash
# Dev-Server starten (Java 21 ist system-weit installiert unter /usr/lib/jvm/java-21-openjdk-amd64)
./mvnw spring-boot:run

# JAR bauen (ohne Tests)
./mvnw package -DskipTests

# JAR ausführen
java -jar target/booking-service-0.0.1-SNAPSHOT.jar

# Tests ausführen
./mvnw test

# Einzelnen Test ausführen
./mvnw test -Dtest=BookingServiceApplicationTests

# Dependency-Baum anzeigen
./mvnw dependency:tree

# Neue Dependency hinzufügen (pom.xml manuell editieren, dann):
./mvnw validate
```

---

## Code Smells — was zu vermeiden ist

| Smell | Erklärung |
|---|---|
| Domain-Logik im Controller | Controller sind Adapter — keine Buchungsregeln, keine Konfliktprüfung dort |
| JPA-Entitäten im REST-Layer zurückgeben | Persistenz-Details lecken nach außen — stattdessen DTOs/Response-Objekte nutzen |
| Repository direkt im Controller aufrufen | Verstößt gegen Hexagonal — immer über einen Port (Use Case) gehen |
| `@Transactional` in Domain-Services | Transaktionen sind Infrastruktur-Concern — gehören in den Adapter OUT oder Application Layer |
| Anämisches Domain-Modell | Entitäten sind keine reinen Datencontainer — Buchungslogik gehört ins Domain-Objekt |
| `new`-Aufrufe für externe Abhängigkeiten | Immer Dependency Injection verwenden, damit Ports austauschbar bleiben |

---

## Run Configurations

### Lokal (Terminal)
```bash
cd backend && ./mvnw spring-boot:run
```

### Hintergrund (für parallelen Frontend-Betrieb)
```bash
java -jar backend/target/booking-service-0.0.1-SNAPSHOT.jar > /tmp/backend.log 2>&1 &
```

Backend läuft dann auf: `http://localhost:8081`
Smoke-Test: `curl http://localhost:8081/api/hello` → `Hello World!`

---

## Weitere wichtige Hinweise

- **Kein CORS nötig**: Das Vite-Frontend proxied alle `/api/*`-Requests intern weiter — der Browser sieht nur eine Origin (Port 3000). Kein `@CrossOrigin` oder CORS-Config im Backend nötig.
- **Ressource-IDs kommen aus dem Frontend**: Der Booking Service kennt Räume/Standorte nur als opake IDs. Die Stammdaten liegen als Mock-Daten im Frontend (`frontend/src/lib/mock-data.ts`). Validierung der IDs erfolgt erst, wenn der Ressource-Service eingeführt wird (TS-001).
- **Basic-Auth-Header**: Im Prototyp übergibt das Frontend den Nutzer-Identifier als Basic-Auth-Username ohne Passwort. Das Backend vertraut diesem Wert ohne Verifikation (TS-002).
- **Port 8081**: Port 8080 ist in der Trainings-Umgebung von code-server belegt.
