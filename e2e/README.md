# E2E Tests (Playwright)

## Voraussetzungen

Backend starten: `cd ../backend && mvn spring-boot:run`
Frontend starten: `cd ../frontend && npm run dev`

## Tests starten

```bash
cd e2e
npm install
npx playwright install chromium
npm test
```

## Gegen andere URL testen (z.B. Crucible-Proxy)

```bash
PLAYWRIGHT_BASE_URL=https://... npm test
```
