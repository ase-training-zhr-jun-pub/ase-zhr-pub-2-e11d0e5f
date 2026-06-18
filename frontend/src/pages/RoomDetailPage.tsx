import { useNavigate, useParams, useSearchParams } from "react-router-dom"
import { ArrowLeft, Users, CalendarDays, Clock, CheckCircle2 } from "lucide-react"

import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { LinkButton } from "@/components/link-button"
import { RAEUME, istVerfuegbar } from "@/lib/mock-data"
import { leseSuchKriterien, formatDatum } from "@/lib/such-kriterien"

// Tagesfenster der Timeline: 08:00–18:00 Uhr.
const TAG_START = 8 * 60
const TAG_ENDE = 18 * 60

function zuMinuten(zeit: string): number {
  const [h, m] = zeit.split(":").map(Number)
  return h * 60 + m
}

/** Position (%) einer Zeit innerhalb des Tagesfensters. */
function position(zeit: string): number {
  const min = zuMinuten(zeit)
  return ((min - TAG_START) / (TAG_ENDE - TAG_START)) * 100
}

export function RoomDetailPage() {
  const { id } = useParams()
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const kriterien = leseSuchKriterien(params)

  const raum = RAEUME.find((r) => r.id === id)

  if (!raum) {
    return (
      <div className="space-y-4">
        <LinkButton to={`/raeume?${params.toString()}`} variant="ghost">
          <ArrowLeft />
          Zurück zur Raumliste
        </LinkButton>
        <Card>
          <CardContent className="py-10 text-center text-muted-foreground">
            Dieser Raum wurde nicht gefunden.
          </CardContent>
        </Card>
      </div>
    )
  }

  const frei = istVerfuegbar(raum, kriterien.von, kriterien.bis)

  function bestaetigen() {
    navigate(`/buchen/${id}/details?${params.toString()}`)
  }

  return (
    <div className="space-y-6">
      <LinkButton to={`/raeume?${params.toString()}`} variant="ghost">
        <ArrowLeft />
        Zurück zur Raumliste
      </LinkButton>

      <Card>
        <CardHeader>
          <div className="flex flex-wrap items-center justify-between gap-2">
            <CardTitle className="text-2xl">{raum.name}</CardTitle>
            <Badge variant={frei ? "brand" : "destructive"}>
              {frei ? "Verfügbar" : "Belegt"}
            </Badge>
          </div>
          <p className="text-sm text-muted-foreground">{raum.standort}</p>
        </CardHeader>

        <CardContent className="space-y-6">
          <div className="flex flex-wrap items-center gap-4">
            <span className="flex items-center gap-1.5 text-sm text-muted-foreground">
              <Users className="size-4" />
              {raum.kapazitaet} Personen
            </span>
            <div className="flex flex-wrap gap-1.5">
              {raum.ausstattung.map((a) => (
                <Badge key={a} variant="secondary">
                  {a}
                </Badge>
              ))}
            </div>
          </div>

          <Separator />

          <div className="space-y-3">
            <h2 className="text-sm font-medium">Verfügbarkeit am Tag</h2>
            <div className="relative h-9 w-full overflow-hidden rounded-md bg-muted">
              {/* Belegte Zeitfenster */}
              {raum.belegteSlots.map((slot, i) => (
                <div
                  key={i}
                  className="absolute top-0 h-full bg-destructive/25"
                  style={{
                    left: `${position(slot.von)}%`,
                    width: `${position(slot.bis) - position(slot.von)}%`,
                  }}
                  title={`Belegt: ${slot.titel} (${slot.von}–${slot.bis})`}
                />
              ))}
              {/* Gewählter Zeitraum */}
              <div
                className={`absolute top-0 h-full border-2 ${
                  frei
                    ? "border-brand bg-brand/15"
                    : "border-destructive bg-destructive/10"
                }`}
                style={{
                  left: `${position(kriterien.von)}%`,
                  width: `${
                    position(kriterien.bis) - position(kriterien.von)
                  }%`,
                }}
                title={`Gewählt: ${kriterien.von}–${kriterien.bis}`}
              />
            </div>
            <div className="flex justify-between text-xs text-muted-foreground">
              <span>08:00</span>
              <span>13:00</span>
              <span>18:00</span>
            </div>
            <div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
              <span className="flex items-center gap-1.5">
                <span className="size-3 rounded-sm border-2 border-brand bg-brand/15" />
                Gewählter Zeitraum
              </span>
              <span className="flex items-center gap-1.5">
                <span className="size-3 rounded-sm bg-destructive/25" />
                Belegt
              </span>
            </div>
          </div>

          <Separator />

          <div className="flex flex-wrap items-center gap-x-6 gap-y-2 text-sm">
            <span className="flex items-center gap-1.5">
              <CalendarDays className="size-4 text-muted-foreground" />
              {formatDatum(kriterien.datum)}
            </span>
            <span className="flex items-center gap-1.5">
              <Clock className="size-4 text-muted-foreground" />
              {kriterien.von} – {kriterien.bis} Uhr
            </span>
          </div>

          {frei ? (
            <div className="space-y-3">
              <Badge variant="brand" className="gap-1">
                <CheckCircle2 className="size-3.5" />
                Ausgewählt
              </Badge>
              <div className="flex flex-wrap items-center gap-4">
                <Button size="lg" className="sm:w-auto" onClick={bestaetigen}>
                  Raumauswahl bestätigen
                </Button>
                <LinkButton to={`/raeume?${params.toString()}`} variant="ghost">
                  Anderen Raum wählen
                </LinkButton>
              </div>
            </div>
          ) : (
            <div className="rounded-md border border-destructive/30 bg-destructive/5 px-4 py-3 text-sm text-destructive">
              Der Raum ist im gewählten Zeitraum bereits belegt. Bitte passen Sie
              die Suche an.
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
