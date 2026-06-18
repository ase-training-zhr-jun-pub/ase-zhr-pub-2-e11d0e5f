import { useState } from "react"
import { useNavigate, useParams, useSearchParams } from "react-router-dom"
import { ArrowLeft, CalendarDays, Clock, MapPin } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Separator } from "@/components/ui/separator"
import { LinkButton } from "@/components/link-button"
import { RAEUME, AKTUELLER_NUTZER } from "@/lib/mock-data"
import { leseSuchKriterien, formatDatum } from "@/lib/such-kriterien"
import { buchungAnlegen } from "@/lib/api"

export function BookingDetailsPage() {
  const { id } = useParams()
  const [params] = useSearchParams()
  const navigate = useNavigate()
  const kriterien = leseSuchKriterien(params)

  const [titel, setTitel] = useState("")
  const [notiz, setNotiz] = useState("")
  const [laeuft, setLaeuft] = useState(false)
  const [fehler, setFehler] = useState<string | null>(null)

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

  async function weiter() {
    setFehler(null)
    setLaeuft(true)
    try {
      const result = await buchungAnlegen(
        {
          raumId: raum!.id,
          datum: kriterien.datum,
          von: kriterien.von,
          bis: kriterien.bis,
          titel,
          notiz: notiz || undefined,
        },
        AKTUELLER_NUTZER.nutzerId,
      )
      navigate("/buchungsbestaetigung", {
        state: {
          buchungsnummer: result.buchungsnummer,
          raumId: raum!.id,
          titel,
          notiz: notiz || undefined,
          datum: kriterien.datum,
          von: kriterien.von,
          bis: kriterien.bis,
          standort: kriterien.standort,
        },
      })
    } catch {
      setFehler("Buchung konnte nicht gespeichert werden.")
    } finally {
      setLaeuft(false)
    }
  }

  return (
    <div className="space-y-6">
      <LinkButton to={`/raeume/${id}?${params.toString()}`} variant="ghost">
        <ArrowLeft />
        Zurück zur Raumauswahl
      </LinkButton>

      <Card>
        <CardHeader>
          <CardTitle>Buchungsdetails</CardTitle>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="rounded-md bg-muted p-4 space-y-2">
            <div className="font-medium">{raum.name}</div>
            <div className="flex flex-wrap gap-4 text-sm text-muted-foreground">
              <span className="flex items-center gap-1.5">
                <MapPin className="size-4" />
                {raum.standort}
              </span>
              <span className="flex items-center gap-1.5">
                <CalendarDays className="size-4" />
                {formatDatum(kriterien.datum)}
              </span>
              <span className="flex items-center gap-1.5">
                <Clock className="size-4" />
                {kriterien.von} – {kriterien.bis} Uhr
              </span>
            </div>
          </div>

          <Separator />

          <div className="space-y-4">
            <div className="space-y-1.5">
              <Label htmlFor="titel">Meetingtitel</Label>
              <Input
                id="titel"
                placeholder="z. B. Sprint Planning"
                value={titel}
                onChange={(e) => setTitel(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="notiz">
                Buchungsnotiz{" "}
                <span className="font-normal text-muted-foreground">
                  (optional)
                </span>
              </Label>
              <textarea
                id="notiz"
                className="flex min-h-[80px] w-full rounded-md border border-input bg-transparent px-3 py-2 text-sm shadow-xs placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                placeholder="Zusätzliche Hinweise zur Buchung …"
                value={notiz}
                onChange={(e) => setNotiz(e.target.value)}
              />
            </div>
          </div>

          {fehler && (
            <p className="text-sm text-destructive">{fehler}</p>
          )}

          <Button
            size="lg"
            className="w-full sm:w-auto"
            onClick={weiter}
            disabled={laeuft || !titel}
          >
            {laeuft ? "Wird gespeichert …" : "Buchung absenden"}
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
