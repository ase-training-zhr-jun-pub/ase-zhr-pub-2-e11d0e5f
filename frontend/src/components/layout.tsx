import { Link, NavLink, Outlet } from "react-router-dom"

import { cn } from "@/lib/utils"
import { BackendStatus } from "@/components/backend-status"
import { AKTUELLER_NUTZER } from "@/lib/mock-data"

const navLinks = [
  { to: "/raeume", label: "Räume" },
  { to: "/meine-buchungen", label: "Meine Buchungen" },
]

export function Layout() {
  return (
    <div className="flex min-h-svh flex-col bg-background">
      <header className="sticky top-0 z-40 border-b bg-background/80 backdrop-blur">
        <div className="mx-auto flex h-14 w-full max-w-5xl items-center gap-6 px-4">
          <Link
            to="/"
            className="text-lg font-semibold tracking-tight text-foreground"
          >
            Calvin
          </Link>

          <nav className="flex items-center gap-1">
            {navLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                className={({ isActive }) =>
                  cn(
                    "border-b-2 px-1 pt-1.5 pb-1 text-sm font-medium transition-colors",
                    isActive
                      ? "border-brand text-foreground"
                      : "border-transparent text-muted-foreground hover:text-foreground",
                  )
                }
              >
                {link.label}
              </NavLink>
            ))}
          </nav>

          <div className="ml-auto flex items-center gap-3">
            <span className="hidden text-sm text-muted-foreground sm:inline">
              {AKTUELLER_NUTZER.name}
            </span>
            <div
              className="flex size-8 items-center justify-center rounded-full bg-primary text-xs font-medium text-primary-foreground"
              title={AKTUELLER_NUTZER.name}
            >
              {AKTUELLER_NUTZER.initialen}
            </div>
          </div>
        </div>
      </header>

      <main className="mx-auto w-full max-w-5xl flex-1 px-4 py-8">
        <Outlet />
      </main>

      <footer className="mx-auto w-full max-w-5xl border-t px-4 py-4">
        <BackendStatus />
      </footer>
    </div>
  )
}
