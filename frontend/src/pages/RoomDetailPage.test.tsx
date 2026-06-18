import { render, screen } from "@testing-library/react"
import { MemoryRouter, Routes, Route } from "react-router-dom"
import { RoomDetailPage } from "./RoomDetailPage"

const KRITERIEN = "standort=Köln&datum=2026-06-17&von=09:00&bis=11:00"

function renderPage(raumId: string, queryString = KRITERIEN) {
  render(
    <MemoryRouter initialEntries={[`/raeume/${raumId}?${queryString}`]}>
      <Routes>
        <Route path="/raeume/:id" element={<RoomDetailPage />} />
      </Routes>
    </MemoryRouter>,
  )
}

describe("RoomDetailPage", () => {
  describe("AK3: Raumdetails werden bei der Auswahl angezeigt", () => {
    beforeEach(() => renderPage("koeln-1-1"))

    it("zeigt den Raumnamen an", () => {
      expect(screen.getByText("Raum 1.1")).toBeInTheDocument()
    })

    it("zeigt den Standort an", () => {
      // "Köln" erscheint zweimal: als raum.standort im CardHeader und als
      // kriterien.standort in der Zeitraum-Zeile → getAllByText verwenden.
      expect(screen.getAllByText("Köln").length).toBeGreaterThanOrEqual(1)
    })

    it("zeigt die Kapazität an", () => {
      expect(screen.getByText(/6 Personen/)).toBeInTheDocument()
    })

    it("zeigt alle Ausstattungsmerkmale an", () => {
      expect(screen.getByText("Bildschirm")).toBeInTheDocument()
      expect(screen.getByText("Whiteboard")).toBeInTheDocument()
    })
  })

  describe("AK4: Gewählter Zeitraum wird angezeigt", () => {
    beforeEach(() => renderPage("koeln-1-1"))

    it("zeigt das Datum im deutschen Format an", () => {
      expect(screen.getByText("17.06.2026")).toBeInTheDocument()
    })

    it("zeigt Von- und Bis-Zeit an", () => {
      expect(screen.getByText(/09:00\s*–\s*11:00 Uhr/)).toBeInTheDocument()
    })
  })

  describe("Fehlerfall", () => {
    it("zeigt eine Fehlermeldung bei unbekannter Raum-ID", () => {
      renderPage("unbekannt")
      expect(
        screen.getByText("Dieser Raum wurde nicht gefunden."),
      ).toBeInTheDocument()
    })
  })
})
