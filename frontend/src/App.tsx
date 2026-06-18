import { createBrowserRouter, RouterProvider } from "react-router-dom"

import { Layout } from "@/components/layout"
import { StartPage } from "@/pages/StartPage"
import { RoomListPage } from "@/pages/RoomListPage"
import { RoomDetailPage } from "@/pages/RoomDetailPage"
import { BookingDetailsPage } from "@/pages/BookingDetailsPage"
import { BookingConfirmationPage } from "@/pages/BookingConfirmationPage"
import { MyBookingsPage } from "@/pages/MyBookingsPage"

const router = createBrowserRouter([
  {
    element: <Layout />,
    children: [
      { path: "/", element: <StartPage /> },
      { path: "/raeume", element: <RoomListPage /> },
      { path: "/raeume/:id", element: <RoomDetailPage /> },
      { path: "/buchen/:id/details", element: <BookingDetailsPage /> },
      { path: "/buchungsbestaetigung", element: <BookingConfirmationPage /> },
      { path: "/meine-buchungen", element: <MyBookingsPage /> },
    ],
  },
])

function App() {
  return <RouterProvider router={router} />
}

export default App
