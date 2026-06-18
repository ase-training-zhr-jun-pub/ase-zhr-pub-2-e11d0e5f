import { test, expect } from '@playwright/test'

test.describe('Raumbuchungsprozess', () => {
  test('kompletter Buchungsflow: Raum suchen, buchen, in Übersicht prüfen', async ({ page }) => {
    // 1. Buchungsübersicht öffnen und warten bis Ladezustand beendet
    await page.goto('/meine-buchungen')
    await page.waitForLoadState('networkidle')
    // Warte bis Ladezustand vergeht
    await expect(page.getByText('Buchungen werden geladen')).not.toBeVisible({ timeout: 10_000 })

    // 2. Startseite öffnen
    await page.goto('/')
    await page.waitForLoadState('domcontentloaded')

    // 3. Standort auswählen: Köln (ist Standard-Wert)
    // Falls nicht bereits "Köln": ShadCN Select-Trigger anklicken
    const standortTrigger = page.locator('#standort')
    const currentStandort = await standortTrigger.textContent()
    if (!currentStandort?.includes('Köln')) {
      await standortTrigger.click()
      await page.getByRole('option', { name: 'Köln' }).click()
    }

    // 4. Datum auswählen (weit in der Zukunft, damit keine Konflikte)
    await page.locator('#datum').fill('2099-03-15')

    // 5. Zeitraum festlegen
    await page.locator('#von').fill('10:00')
    await page.locator('#bis').fill('12:00')

    // Räume suchen
    await page.getByRole('button', { name: 'Räume suchen' }).click()
    await page.waitForURL('**/raeume**')
    await page.waitForLoadState('domcontentloaded')

    // 6. Raum auswählen: "Raum 1.2" (keine belegten Slots, immer verfügbar)
    const raumKarte = page.locator('h3, h2, [class*="CardTitle"]').filter({ hasText: 'Raum 1.2' }).first()
    await expect(raumKarte).toBeVisible({ timeout: 5_000 })
    await raumKarte.click()
    await page.waitForURL('**/raeume/**')
    await page.waitForLoadState('networkidle')

    // Verfügbarkeit prüfen (Akzeptanzkriterium CLVN-010)
    await expect(page.getByText('Verfügbar')).toBeVisible({ timeout: 10_000 })

    // Raumauswahl bestätigen
    await page.getByRole('button', { name: 'Raumauswahl bestätigen' }).click()
    await page.waitForURL('**/buchen/**')

    // Meetingtitel eingeben (Pflichtfeld)
    const titelInput = page.getByLabel('Meetingtitel')
    await expect(titelInput).toBeVisible()
    await titelInput.fill('E2E Test Meeting')

    // 7. Buchen: Button erst klickbar wenn Titel ausgefüllt
    const buchungsButton = page.getByRole('button', { name: 'Buchung absenden' })
    await expect(buchungsButton).toBeEnabled()
    await buchungsButton.click()

    // 8. Buchungsbestätigung erscheint
    await expect(page.getByText('Buchung bestätigt!')).toBeVisible({ timeout: 10_000 })
    await expect(page.getByText('Ihr Konferenzraum ist verbindlich für Sie reserviert.')).toBeVisible()

    // 9. Buchungsübersicht öffnen und neue Buchung verifizieren
    await page.getByRole('link', { name: 'Meine Buchungen' }).click()
    await page.waitForURL('**/meine-buchungen**')
    await page.waitForLoadState('networkidle')
    await expect(page.getByText('Buchungen werden geladen')).not.toBeVisible({ timeout: 10_000 })

    // Neue Buchung "E2E Test Meeting" muss in der Liste erscheinen
    await expect(page.getByText('E2E Test Meeting')).toBeVisible({ timeout: 10_000 })
  })

  test('Button "Buchung absenden" ist deaktiviert wenn Meetingtitel leer', async ({ page }) => {
    // Navigiere direkt zur BookingDetailsPage mit korrekten Params
    await page.goto('/buchen/koeln-1-2/details?standort=K%C3%B6ln&datum=2099-03-15&von=10%3A00&bis=12%3A00')
    await page.waitForLoadState('domcontentloaded')

    const button = page.getByRole('button', { name: 'Buchung absenden' })
    await expect(button).toBeDisabled()

    await page.getByLabel('Meetingtitel').fill('Test')
    await expect(button).toBeEnabled()
  })
})
