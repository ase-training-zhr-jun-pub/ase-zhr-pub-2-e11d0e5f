import path from "path"
import tailwindcss from "@tailwindcss/vite"
import react from "@vitejs/plugin-react"
import { defineConfig } from "vitest/config"
import type { Plugin } from "vite"

// Betrieb hinter dem Crucible-Proxy — siehe .claude/rules/betrieb-hinter-proxy.md
//
// Port zentral festlegen: er steckt im Proxy-Pfad (base) UND im Dev-Server —
// beide MÜSSEN denselben Port nutzen. Die App wird im Browser über
// …/proxy/3000/ geöffnet.
const PORT = 3000
const proxyUri = process.env.VSCODE_PROXY_URI
const base = proxyUri
  ? new URL(proxyUri.replace("{{port}}", String(PORT))).pathname
  : "/"

// Der Proxy strippt den Pfad-Prefix beim Weiterleiten -> wir hängen ihn für
// eingehende Requests wieder an, damit Vite die Dateien findet. Dadurch arbeitet
// Vite vollständig im "base-prefixed"-Modus und erzeugt alle Client-URLs (auch
// node_modules/.vite/deps/*) konsistent mit Prefix.
function proxyBasePlugin(): Plugin {
  return {
    name: "proxy-base-rewrite",
    apply: "serve",
    configureServer(server) {
      server.middlewares.use((req, _res, next) => {
        // /api unverändert lassen -> wird über server.proxy ans Backend geleitet.
        if (
          base !== "/" &&
          req.url &&
          !req.url.startsWith(base) &&
          !req.url.startsWith("/api")
        ) {
          req.url = base.replace(/\/$/, "") + req.url
        }
        next()
      })
    },
  }
}

export default defineConfig({
  base,
  plugins: [react(), tailwindcss(), proxyBasePlugin()],
  server: {
    host: "0.0.0.0", // Proxy greift unter beliebigem Host zu
    port: PORT, // Port pinnen, damit `npm run dev` nicht auf 5173 landet
    allowedHosts: true, // fremden Crucible-Host akzeptieren
    // /api-Anfragen an den Booking Service (Spring Boot) weiterleiten.
    // So spricht der Browser nur die Vite-Origin an -> kein CORS nötig.
    proxy: {
      "/api": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    environment: "jsdom",
    globals: true,
    setupFiles: ["./src/test/setup.ts"],
  },
})
