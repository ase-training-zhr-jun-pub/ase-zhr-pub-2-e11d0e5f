import { useState } from "react"
import { useNavigate } from "react-router-dom"
import { Search } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { STANDORTE } from "@/lib/mock-data"

export function StartPage() {
  const navigate = useNavigate()
  const [standort, setStandort] = useState<string>("Köln")
  const [datum, setDatum] = useState("2026-06-17")
  const [von, setVon] = useState("09:00")
  const [bis, setBis] = useState("11:00")

  function sucheStarten() {
    const params = new URLSearchParams({ standort, datum, von, bis })
    navigate(`/raeume?${params.toString()}`)
  }

  return (
    <div className="flex flex-col items-center gap-10 pt-10">
      <div className="max-w-2xl space-y-3 text-center">
        <h1 className="text-3xl font-semibold tracking-tight text-foreground sm:text-4xl">
          Mühelos den passenden Raum finden
        </h1>
        <p className="text-muted-foreground">
          Konferenzräume an allen acht INNOQ-Standorten – buchen Sie sicher und
          ohne Doppelbuchungen.
        </p>
      </div>

      <Card className="w-full max-w-3xl">
        <CardContent className="pt-6">
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <div className="flex flex-col gap-1.5">
              <Label htmlFor="standort">Standort</Label>
              <Select
                value={standort}
                onValueChange={(value) => value && setStandort(value)}
              >
                <SelectTrigger id="standort" className="w-full">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {STANDORTE.map((s) => (
                    <SelectItem key={s} value={s}>
                      {s}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="datum">Datum</Label>
              <Input
                id="datum"
                type="date"
                value={datum}
                onChange={(e) => setDatum(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="von">Von</Label>
              <Input
                id="von"
                type="time"
                value={von}
                onChange={(e) => setVon(e.target.value)}
              />
            </div>

            <div className="flex flex-col gap-1.5">
              <Label htmlFor="bis">Bis</Label>
              <Input
                id="bis"
                type="time"
                value={bis}
                onChange={(e) => setBis(e.target.value)}
              />
            </div>
          </div>

          <Button className="mt-6 w-full sm:w-auto" onClick={sucheStarten}>
            <Search />
            Räume suchen
          </Button>
        </CardContent>
      </Card>
    </div>
  )
}
