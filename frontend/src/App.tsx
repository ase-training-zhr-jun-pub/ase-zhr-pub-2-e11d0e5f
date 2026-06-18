import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card"

function App() {
  return (
    <div className="flex min-h-svh items-center justify-center bg-background p-8">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle>Calvin</CardTitle>
          <CardDescription>INNOQ Raumbuchungssystem</CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          <p className="text-sm text-muted-foreground">
            Du kannst jetzt mit dem Aufbau der UI
            beginnen.
          </p>
        </CardContent>
      </Card>
    </div>
  )
}

export default App
