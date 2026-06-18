import { useSearchParams } from "react-router-dom"
import { CheckCircle2, CalendarDays, Clock, MapPin, Hash } from "lucide-react"

import { Card, CardContent } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { LinkButton } from "@/components/link-button"
import { RAEUME } from "@/lib/mock-data"
import { leseSuchKriterien, formatDatum } from "@/lib/such-kriterien"

export function BookingConfirmationPage() {
  const [params] = useSearchParams()
  const kriterien = leseSuchKriterien(params)
  const raumId = params.get("raum") ?? ""
  const buchungsnummer = params.get("buchungsnummer") ?? ""
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
            <p className="text-lg font-medium">{raum?.name ?? "Raum"}</p>
            <p className="flex items-center gap-1.5 text-sm text-muted-foreground">
              <MapPin className="size-4" />
              {kriterien.standort}
            </p>
          </div>

          <Separator />

          <div className="space-y-2 text-sm">
            <p className="flex items-center gap-2">
              <CalendarDays className="size-4 text-muted-foreground" />
              {formatDatum(kriterien.datum)}
            </p>
            <p className="flex items-center gap-2">
              <Clock className="size-4 text-muted-foreground" />
              {kriterien.von} – {kriterien.bis} Uhr
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
