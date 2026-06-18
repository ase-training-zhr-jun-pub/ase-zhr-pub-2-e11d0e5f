// Aufrufe an den Booking Service (Spring Boot).
//
// BASE_URL ist hinter dem Crucible-Proxy der Proxy-Unterpfad (…/proxy/3000/),
// lokal "/". Wir hängen "api/…" an, sodass der Request über den Vite-Dev-Proxy
// (server.proxy "/api") an das Backend weitergeleitet wird — siehe vite.config.ts.
const API_BASE = import.meta.env.BASE_URL // endet immer mit "/"

function basicAuthHeader(nutzerId: string): string {
  return "Basic " + btoa(`${nutzerId}:`)
}

/** Smoke-Test: fragt /api/hello ab und liefert die Antwort als Text. */
export async function fetchHello(): Promise<string> {
  const res = await fetch(`${API_BASE}api/hello`)
  if (!res.ok) {
    throw new Error(`HTTP ${res.status}`)
  }
  return res.text()
}

export interface BuchungAnlegenRequest {
  raumId: string
  datum: string
  von: string
  bis: string
  titel: string
  notiz?: string
}

export interface BackendBuchung {
  buchungsnummer: string
  raumId: string
  datum: string
  von: string
  bis: string
  titel: string
  nutzerId: string
}

export async function buchungAnlegen(
  req: BuchungAnlegenRequest,
  nutzerId: string,
): Promise<{ buchungsnummer: string }> {
  const res = await fetch(`${API_BASE}api/buchungen`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      Authorization: basicAuthHeader(nutzerId),
    },
    body: JSON.stringify(req),
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.json()
}

export async function buchungenAbrufen(nutzerId: string): Promise<BackendBuchung[]> {
  const res = await fetch(`${API_BASE}api/buchungen`, {
    headers: { Authorization: basicAuthHeader(nutzerId) },
  })
  if (!res.ok) throw new Error(`HTTP ${res.status}`)
  return res.json()
}
