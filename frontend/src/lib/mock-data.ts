// Zentrale Mock-Daten für den Calvin-Prototyp.
// Kein Backend — alle Daten werden hier gemockt (siehe Prototyp-Scope).

/** Die acht INNOQ-Standorte (siehe Glossar: Standort). */
export const STANDORTE = [
  "Monheim",
  "Berlin",
  "Hamburg",
  "Köln",
  "München",
  "Zürich",
  "Baar",
  "Offenbach",
] as const

export type Standort = (typeof STANDORTE)[number]

/** Mögliche Ausstattungsmerkmale eines Konferenzraums (siehe Glossar: Ausstattung). */
export type Ausstattung =
  | "Bildschirm"
  | "Whiteboard"
  | "Videokonferenz"
  | "Telefonkonferenz"

export interface Konferenzraum {
  id: string
  name: string
  standort: Standort
  kapazitaet: number
  ausstattung: Ausstattung[]
  /** Belegte Zeitfenster am ausgewählten Tag (Stunden im 24h-Format). */
  belegteSlots: { von: string; bis: string; titel: string }[]
}

export interface Buchung {
  id: string
  buchungsnummer: string
  raumName: string
  standort: Standort
  datum: string // ISO: YYYY-MM-DD
  von: string
  bis: string
  titel: string
}

/** Verfügbare Konferenzräume je Standort. */
export const RAEUME: Konferenzraum[] = [
  {
    id: "koeln-1-1",
    name: "Raum 1.1",
    standort: "Köln",
    kapazitaet: 6,
    ausstattung: ["Bildschirm", "Whiteboard"],
    belegteSlots: [{ von: "13:00", bis: "14:00", titel: "Team-Sync" }],
  },
  {
    id: "koeln-1-2",
    name: "Raum 1.2",
    standort: "Köln",
    kapazitaet: 10,
    ausstattung: ["Bildschirm", "Videokonferenz"],
    belegteSlots: [],
  },
  {
    id: "koeln-2-1",
    name: "Raum 2.1",
    standort: "Köln",
    kapazitaet: 4,
    ausstattung: ["Whiteboard"],
    belegteSlots: [
      { von: "09:00", bis: "11:00", titel: "Kundenworkshop" },
      { von: "14:00", bis: "15:30", titel: "Retro" },
    ],
  },
  {
    id: "koeln-boardroom",
    name: "Boardroom",
    standort: "Köln",
    kapazitaet: 20,
    ausstattung: ["Bildschirm", "Videokonferenz", "Whiteboard"],
    belegteSlots: [],
  },
  {
    id: "berlin-fenster",
    name: "Fensterraum",
    standort: "Berlin",
    kapazitaet: 8,
    ausstattung: ["Bildschirm", "Whiteboard", "Videokonferenz"],
    belegteSlots: [{ von: "10:00", bis: "12:00", titel: "Architektur-Review" }],
  },
  {
    id: "berlin-fokus",
    name: "Fokusraum",
    standort: "Berlin",
    kapazitaet: 3,
    ausstattung: ["Telefonkonferenz"],
    belegteSlots: [],
  },
  {
    id: "hamburg-hafen",
    name: "Hafenblick",
    standort: "Hamburg",
    kapazitaet: 12,
    ausstattung: ["Bildschirm", "Videokonferenz"],
    belegteSlots: [],
  },
  {
    id: "muenchen-alpen",
    name: "Alpenraum",
    standort: "München",
    kapazitaet: 6,
    ausstattung: ["Bildschirm", "Whiteboard"],
    belegteSlots: [{ von: "09:00", bis: "10:00", titel: "Daily" }],
  },
  {
    id: "zuerich-see",
    name: "Seeblick",
    standort: "Zürich",
    kapazitaet: 8,
    ausstattung: ["Bildschirm", "Videokonferenz", "Whiteboard"],
    belegteSlots: [],
  },
]

/** Eigene Buchungen des angemeldeten Mitarbeiters (Persona: Alex Berger). */
export const MEINE_BUCHUNGEN: Buchung[] = [
  {
    id: "b-1001",
    buchungsnummer: "CLVN-2026-0042",
    raumName: "Raum 1.1",
    standort: "Köln",
    datum: "2026-06-17",
    von: "09:00",
    bis: "11:00",
    titel: "Team-Meeting",
  },
  {
    id: "b-1002",
    buchungsnummer: "CLVN-2026-0051",
    raumName: "Boardroom",
    standort: "Berlin",
    datum: "2026-06-24",
    von: "14:00",
    bis: "16:00",
    titel: "Kundenworkshop",
  },
  {
    id: "b-0987",
    buchungsnummer: "CLVN-2026-0033",
    raumName: "Raum 2.1",
    standort: "Köln",
    datum: "2026-06-10",
    von: "10:00",
    bis: "12:00",
    titel: "Sprint-Planning",
  },
]

/** Der aktuell angemeldete Mitarbeiter (gemockt). */
export const AKTUELLER_NUTZER = {
  name: "Alex Berger",
  initialen: "AB",
  rolle: "Senior Consultant",
  nutzerId: "alex.berger",
}

/** Standard-Zeitslots für die Auswahl (volle/halbe Stunden, 08:00–18:00). */
export const ZEIT_SLOTS: string[] = (() => {
  const slots: string[] = []
  for (let h = 8; h <= 18; h++) {
    slots.push(`${String(h).padStart(2, "0")}:00`)
    if (h < 18) slots.push(`${String(h).padStart(2, "0")}:30`)
  }
  return slots
})()

/** Prüft, ob ein Raum im gewünschten Zeitraum verfügbar ist (Verfügbarkeitsanzeige). */
export function istVerfuegbar(
  raum: Konferenzraum,
  von: string,
  bis: string,
): boolean {
  return !raum.belegteSlots.some(
    (slot) => von < slot.bis && bis > slot.von,
  )
}
