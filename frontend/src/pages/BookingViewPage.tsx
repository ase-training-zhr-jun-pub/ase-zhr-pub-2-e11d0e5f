import { useLocation } from "react-router-dom"
import { ArrowLeft, CalendarDays, Clock, MapPin, Hash } from "lucide-react"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Separator } from "@/components/ui/separator"
import { LinkButton } from "@/components/link-button"

interface BookingViewState {
  buchungsnummer: string
  raumName: string
  standort: string
  datum: string
  von: string
  bis: string
  titel: string
}

export function BookingViewPage() {
  const location = useLocation()
  const state = location.state as BookingViewState | null

  if (!state) {
    return (
      <div className="space-y-4">
        <LinkButton to="/meine-buchungen" variant="ghost">
          <ArrowLeft /> Zurück
        </LinkButton>
        <Card>
          <CardContent className="py-10 text-center text-muted-foreground">
            Buchungsdetails nicht verfügbar.
          </CardContent>
        </Card>
      </div>
    )
  }

  // Datum formatieren (YYYY-MM-DD → "Mo., 17. Jun. 2026")
  function formatDatum(iso: string): string {
    const [year, month, day] = iso.split("-").map(Number)
    return new Date(year, month - 1, day).toLocaleDateString("de-DE", {
      weekday: "short", day: "numeric", month: "short", year: "numeric",
    })
  }

  return (
    <div className="space-y-6">
      <LinkButton to="/meine-buchungen" variant="ghost">
        <ArrowLeft /> Meine Buchungen
      </LinkButton>
      <Card>
        <CardHeader>
          <CardTitle>{state.titel || "Buchungsdetails"}</CardTitle>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center gap-2 text-sm text-muted-foreground">
            <MapPin className="size-4" /> {state.raumName} · {state.standort}
          </div>
          <Separator />
          <div className="space-y-2 text-sm">
            <p className="flex items-center gap-2">
              <CalendarDays className="size-4 text-muted-foreground" />
              {formatDatum(state.datum)}
            </p>
            <p className="flex items-center gap-2">
              <Clock className="size-4 text-muted-foreground" />
              {state.von} – {state.bis} Uhr
            </p>
            <p className="flex items-center gap-2 text-muted-foreground">
              <Hash className="size-4" /> {state.buchungsnummer}
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  )
}
