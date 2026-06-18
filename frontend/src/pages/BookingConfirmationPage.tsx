import { useLocation } from "react-router-dom"
import { CheckCircle2, CalendarDays, Clock, MapPin, Hash } from "lucide-react"

import { Card, CardContent } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { LinkButton } from "@/components/link-button"
import { RAEUME } from "@/lib/mock-data"
import { formatDatum } from "@/lib/such-kriterien"

export function BookingConfirmationPage() {
  const location = useLocation()
  const state = location.state as {
    buchungsnummer: string
    raumId: string
    titel: string
    notiz?: string
    datum: string
    von: string
    bis: string
    standort: string
  } | null

  if (!state) {
    return <div className="pt-12 text-center text-muted-foreground">Keine Buchungsdetails verfügbar.</div>
  }

  const { buchungsnummer, raumId, titel, notiz, datum, von, bis, standort } = state
  const raum = RAEUME.find((r) => r.id === raumId)

  return (
    <div className="flex flex-col items-center gap-8 pt-12">
      <div className="flex flex-col items-center gap-3 text-center">
        <CheckCircle2 className="size-14 text-primary" />
        <h1 className="text-2xl font-semibold tracking-tight">
          Buchung bestätigt!
        </h1>
        <p className="text-muted-foreground">
          Ihr Konferenzraum ist verbindlich für Sie reserviert.
        </p>
      </div>

      <Card className="w-full max-w-md">
        <CardContent className="space-y-4 pt-6">
          <div className="space-y-1">
            <p className="text-lg font-medium">{titel || raum?.name ?? "Raum"}</p>
            <p className="flex items-center gap-1.5 text-sm text-muted-foreground">
              <MapPin className="size-4" />
              {raum?.name ?? raumId} · {standort}
            </p>
            {notiz && (
              <p className="text-sm text-muted-foreground">{notiz}</p>
            )}
          </div>

          <Separator />

          <div className="space-y-2 text-sm">
            <p className="flex items-center gap-2">
              <CalendarDays className="size-4 text-muted-foreground" />
              {formatDatum(datum)}
            </p>
            <p className="flex items-center gap-2">
              <Clock className="size-4 text-muted-foreground" />
              {von} – {bis} Uhr
            </p>
            <p className="flex items-center gap-2 text-muted-foreground">
              <Hash className="size-4" />
              {buchungsnummer}
            </p>
          </div>
        </CardContent>
      </Card>

      <div className="flex flex-wrap justify-center gap-3">
        <LinkButton to="/" variant="outline">
          Weitere Buchung
        </LinkButton>
        <LinkButton to="/meine-buchungen">Meine Buchungen</LinkButton>
      </div>
    </div>
  )
}
