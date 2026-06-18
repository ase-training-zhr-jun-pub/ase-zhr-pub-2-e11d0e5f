import { useSearchParams } from "react-router-dom"
import { Users, Pencil } from "lucide-react"

import { Badge } from "@/components/ui/badge"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { LinkButton } from "@/components/link-button"
import { RAEUME, istVerfuegbar } from "@/lib/mock-data"
import { leseSuchKriterien, formatDatum } from "@/lib/such-kriterien"

export function RoomListPage() {
  const [params] = useSearchParams()
  const kriterien = leseSuchKriterien(params)

  const raeume = RAEUME.filter((r) => r.standort === kriterien.standort)

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-2xl font-semibold tracking-tight">
            Verfügbare Räume
          </h1>
          <p className="text-sm text-muted-foreground">
            {kriterien.standort} · {formatDatum(kriterien.datum)} ·{" "}
            {kriterien.von}–{kriterien.bis} Uhr
          </p>
        </div>
        <LinkButton to="/" variant="outline">
          Suche ändern
        </LinkButton>
      </div>

      {raeume.length === 0 ? (
        <Card>
          <CardContent className="py-10 text-center text-muted-foreground">
            Für {kriterien.standort} sind aktuell keine Räume hinterlegt.
          </CardContent>
        </Card>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2">
          {raeume.map((raum) => {
            const frei = istVerfuegbar(raum, kriterien.von, kriterien.bis)
            return (
              <Card key={raum.id} className="flex flex-col">
                <CardHeader>
                  <div className="flex items-start justify-between gap-2">
                    <CardTitle>{raum.name}</CardTitle>
                    <Badge variant={frei ? "brand" : "destructive"}>
                      {frei ? "Verfügbar" : "Belegt"}
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent className="flex flex-1 flex-col gap-4">
                  <div className="flex items-center gap-1.5 text-sm text-muted-foreground">
                    <Users className="size-4" />
                    {raum.kapazitaet} Personen
                  </div>
                  <div className="flex flex-wrap gap-1.5">
                    {raum.ausstattung.map((a) => (
                      <Badge key={a} variant="secondary">
                        {a}
                      </Badge>
                    ))}
                  </div>
                  <LinkButton
                    to={`/raeume/${raum.id}?${params.toString()}`}
                    variant="outline"
                    className="mt-auto w-full"
                  >
                    <Pencil />
                    Details ansehen
                  </LinkButton>
                </CardContent>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}
