// Liest die Such-Kriterien (Standort, Datum, Zeitraum) aus den URL-Query-Parametern.
// So bleibt der Buchungskontext beim Navigieren in der URL erhalten.

export interface SuchKriterien {
  standort: string
  datum: string
  von: string
  bis: string
}

export function leseSuchKriterien(params: URLSearchParams): SuchKriterien {
  return {
    standort: params.get("standort") ?? "Köln",
    datum: params.get("datum") ?? "2026-06-17",
    von: params.get("von") ?? "09:00",
    bis: params.get("bis") ?? "11:00",
  }
}

/** Formatiert ein ISO-Datum (YYYY-MM-DD) als deutsches Datum (TT.MM.JJJJ). */
export function formatDatum(iso: string): string {
  const [jahr, monat, tag] = iso.split("-")
  if (!jahr || !monat || !tag) return iso
  return `${tag}.${monat}.${jahr}`
}
