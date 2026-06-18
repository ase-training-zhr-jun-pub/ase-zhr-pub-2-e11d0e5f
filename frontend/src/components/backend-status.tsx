import { useEffect, useState } from "react"
import { CheckCircle2, Loader2, XCircle } from "lucide-react"

import { fetchHello } from "@/lib/api"

type Status =
  | { state: "laden" }
  | { state: "ok"; nachricht: string }
  | { state: "fehler"; detail: string }

/**
 * Zeigt den Verbindungsstatus zum Booking Service an, indem /api/hello
 * abgefragt und die Antwort ausgegeben wird. Dient als Smoke-Test für die
 * Front-/Backend-Verbindung.
 */
export function BackendStatus() {
  const [status, setStatus] = useState<Status>({ state: "laden" })

  useEffect(() => {
    let aktiv = true
    fetchHello()
      .then((text) => aktiv && setStatus({ state: "ok", nachricht: text }))
      .catch((e) => aktiv && setStatus({ state: "fehler", detail: String(e) }))
    return () => {
      aktiv = false
    }
  }, [])

  if (status.state === "laden") {
    return (
      <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
        <Loader2 className="size-3.5 animate-spin" />
        Backend-Verbindung wird geprüft …
      </span>
    )
  }

  if (status.state === "ok") {
    return (
      <span className="flex items-center gap-1.5 text-xs text-muted-foreground">
        <CheckCircle2 className="size-3.5 text-brand" />
        Booking Service verbunden — Antwort: „{status.nachricht}"
      </span>
    )
  }

  return (
    <span className="flex items-center gap-1.5 text-xs text-destructive">
      <XCircle className="size-3.5" />
      Booking Service nicht erreichbar ({status.detail})
    </span>
  )
}
