import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import { ChevronLeft, ChevronRight } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { LinkButton } from "@/components/link-button"
import { cn } from "@/lib/utils"
import { RAEUME, AKTUELLER_NUTZER, type Buchung, type Standort } from "@/lib/mock-data"
import { buchungenAbrufen } from "@/lib/api"

const HEUTE = new Date().toISOString().split("T")[0]

const WOCHENTAGE = ["Mo", "Di", "Mi", "Do", "Fr", "Sa", "So"]
const MONATE = [
  "Januar", "Februar", "März", "April", "Mai", "Juni",
  "Juli", "August", "September", "Oktober", "November", "Dezember",
]

function pad(n: number): string {
  return String(n).padStart(2, "0")
}

// ISO-Datum (YYYY-MM-DD) zerlegen; Monat 0-basiert für Date-Kompatibilität.
function isoZuDatum(iso: string): { jahr: number; monat: number } {
  const [jahr, monat] = iso.split("-").map(Number)
  return { jahr, monat: monat - 1 }
}

function datumFormatieren(iso: string): string {
  const [jahr, monat, tag] = iso.split("-").map(Number)
  const d = new Date(jahr, monat - 1, tag)
  const wt = WOCHENTAGE[(d.getDay() + 6) % 7]
  return `${wt}, ${tag}. ${MONATE[monat - 1].substring(0, 3)}. ${jahr}`
}

export function MyBookingsPage() {
  const navigate = useNavigate()
  const heute = isoZuDatum(HEUTE)
  const [ansicht, setAnsicht] = useState(heute)
  const [meineBuchungen, setMeineBuchungen] = useState<Buchung[]>([])
  const [ladet, setLadet] = useState(true)
  const [ladeHinweis, setLadeHinweis] = useState<string | null>(null)

  useEffect(() => {
    setLadet(true)
    setLadeHinweis(null)
    buchungenAbrufen(AKTUELLER_NUTZER.nutzerId)
      .then((backendBuchungen) => {
        const buchungen: Buchung[] = backendBuchungen.map((b) => {
          const raum = RAEUME.find((r) => r.id === b.raumId)
          return {
            id: b.buchungsnummer,
            buchungsnummer: b.buchungsnummer,
            raumName: raum?.name ?? b.raumId,
            standort: (raum?.standort ?? "") as Standort,
            datum: b.datum,
            von: b.von,
            bis: b.bis,
            titel: b.titel,
          }
        })
        setMeineBuchungen(buchungen)
      })
      .catch(() => {
        setMeineBuchungen([])
        setLadeHinweis("Buchungen konnten nicht geladen werden.")
      })
      .finally(() => {
        setLadet(false)
      })
  }, [])

  // Buchungen nach ISO-Datum gruppieren und je Tag nach Startzeit sortieren.
  const buchungenProTag = new Map<string, Buchung[]>()
  for (const b of meineBuchungen) {
    const liste = buchungenProTag.get(b.datum) ?? []
    liste.push(b)
    buchungenProTag.set(b.datum, liste)
  }
  for (const liste of buchungenProTag.values()) {
    liste.sort((a, b) => a.von.localeCompare(b.von))
  }

  // Kalenderraster aufbauen (Montag-first, mit Leerzellen am Rand).
  const ersterWochentag = (new Date(ansicht.jahr, ansicht.monat, 1).getDay() + 6) % 7
  const tageImMonat = new Date(ansicht.jahr, ansicht.monat + 1, 0).getDate()
  const zellen: (number | null)[] = [
    ...Array(ersterWochentag).fill(null),
    ...Array.from({ length: tageImMonat }, (_, i) => i + 1),
  ]
  while (zellen.length % 7 !== 0) zellen.push(null)

  function monatWechseln(delta: number) {
    setAnsicht((a) => {
      const d = new Date(a.jahr, a.monat + delta, 1)
      return { jahr: d.getFullYear(), monat: d.getMonth() }
    })
  }

  const buchungenImMonat = meineBuchungen.filter((b) => {
    const { jahr, monat } = isoZuDatum(b.datum)
    return jahr === ansicht.jahr && monat === ansicht.monat
  }).length

  if (ladet) {
    return (
      <div className="space-y-6">
        <h1 className="text-2xl font-semibold tracking-tight">Meine Buchungen</h1>
        <Card>
          <CardContent className="py-10 text-center text-muted-foreground">
            Buchungen werden geladen …
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <h1 className="text-2xl font-semibold tracking-tight">Meine Buchungen</h1>
        <div className="flex items-center gap-2">
          <Button
            variant="outline"
            size="icon"
            onClick={() => monatWechseln(-1)}
            aria-label="Vorheriger Monat"
          >
            <ChevronLeft />
          </Button>
          <span className="min-w-36 text-center text-sm font-medium">
            {MONATE[ansicht.monat]} {ansicht.jahr}
          </span>
          <Button
            variant="outline"
            size="icon"
            onClick={() => monatWechseln(1)}
            aria-label="Nächster Monat"
          >
            <ChevronRight />
          </Button>
        </div>
      </div>

      {ladeHinweis && (
        <p className="text-sm text-muted-foreground">{ladeHinweis}</p>
      )}

      {meineBuchungen.length === 0 ? (
        <Card>
          <CardContent className="flex flex-col items-center gap-4 py-12 text-center">
            <p className="text-muted-foreground">
              Sie haben noch keine Raumbuchungen.
            </p>
            <LinkButton to="/">Raum buchen</LinkButton>
          </CardContent>
        </Card>
      ) : (
        <Card>
          <CardContent className="p-2 sm:p-4">
            <div className="grid grid-cols-7">
              {WOCHENTAGE.map((wt) => (
                <div
                  key={wt}
                  className="px-2 py-1 text-center text-xs font-medium text-muted-foreground"
                >
                  {wt}
                </div>
              ))}
            </div>

            <div className="grid grid-cols-7 gap-px overflow-hidden rounded-md bg-border">
              {zellen.map((tag, i) => {
                if (tag === null) {
                  return <div key={i} className="min-h-24 bg-muted/30" />
                }
                const iso = `${ansicht.jahr}-${pad(ansicht.monat + 1)}-${pad(tag)}`
                const tagesbuchungen = buchungenProTag.get(iso) ?? []
                const istHeute = iso === HEUTE

                return (
                  <div key={i} className="min-h-24 bg-background p-1.5">
                    <div
                      className={cn(
                        "mb-1 flex size-6 items-center justify-center rounded-full text-xs",
                        istHeute
                          ? "bg-brand font-medium text-brand-foreground"
                          : "text-muted-foreground",
                      )}
                    >
                      {tag}
                    </div>

                    <div className="space-y-1">
                      {tagesbuchungen.map((b) => {
                        const vergangen = b.datum < HEUTE
                        return (
                          <div
                            key={b.id}
                            title={`${b.titel} · ${b.raumName} · ${b.standort} · ${b.von}–${b.bis} Uhr`}
                            className={cn(
                              "truncate rounded px-1.5 py-1 text-xs leading-tight",
                              vergangen
                                ? "bg-muted text-muted-foreground"
                                : "border border-brand/30 bg-brand/10 text-foreground",
                            )}
                          >
                            <span className="font-medium">{b.von}</span> {b.titel}
                          </div>
                        )
                      })}
                    </div>
                  </div>
                )
              })}
            </div>
          </CardContent>
        </Card>
      )}

      {meineBuchungen.length > 0 && (
        <div className="flex flex-wrap items-center gap-x-5 gap-y-2 text-xs text-muted-foreground">
          <span className="flex items-center gap-1.5">
            <span className="size-3 rounded border border-brand/30 bg-brand/10" />
            Bevorstehend
          </span>
          <span className="flex items-center gap-1.5">
            <span className="size-3 rounded bg-muted" />
            Vergangen
          </span>
          {buchungenImMonat === 0 && (
            <span>Keine Buchungen in {MONATE[ansicht.monat]} {ansicht.jahr}.</span>
          )}
        </div>
      )}

      <section className="space-y-3">
        <h2 className="text-lg font-semibold tracking-tight">Alle Buchungen</h2>
        {meineBuchungen.length === 0 ? (
          <Card>
            <CardContent className="py-8 text-center text-muted-foreground">
              Sie haben noch keine Raumbuchungen.
            </CardContent>
          </Card>
        ) : (
          <div className="space-y-2">
            {[...meineBuchungen]
              .sort((a, b) =>
                new Date(`${a.datum}T${a.von}`).getTime() - new Date(`${b.datum}T${b.von}`).getTime(),
              )
              .map((b) => {
                const vergangen = b.datum < HEUTE
                return (
                  <Card
                    key={b.id}
                    title={`${b.titel} · ${b.raumName} · ${b.standort} · ${b.von}–${b.bis} Uhr`}
                    className={cn(
                      "cursor-pointer transition-colors hover:bg-muted/50",
                      vergangen && "opacity-60",
                    )}
                    onClick={() =>
                      navigate(
                        `/buchungsdetails/${b.buchungsnummer}?datum=${b.datum}&von=${b.von}&bis=${b.bis}&raumName=${encodeURIComponent(b.raumName)}&standort=${encodeURIComponent(b.standort)}&titel=${encodeURIComponent(b.titel)}`,
                      )
                    }
                  >
                    <CardContent className="flex flex-wrap items-center gap-x-6 gap-y-1 py-3">
                      <span className="min-w-40 text-sm font-medium">
                        {datumFormatieren(b.datum)}
                      </span>
                      <span className="text-sm text-muted-foreground">
                        {b.von}–{b.bis} Uhr
                      </span>
                      <span className="text-sm font-medium">{b.raumName}</span>
                      <span className="text-sm text-muted-foreground">{b.standort}</span>
                      <span className="text-sm">{b.titel}</span>
                    </CardContent>
                  </Card>
                )
              })}
          </div>
        )}
      </section>
    </div>
  )
}
