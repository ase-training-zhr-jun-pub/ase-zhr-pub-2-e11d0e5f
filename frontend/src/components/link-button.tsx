import { Link, type LinkProps } from "react-router-dom"

import { Button } from "@/components/ui/button"

type ButtonProps = React.ComponentProps<typeof Button>

/**
 * Ein als react-router-`Link` gerenderter Button.
 * Setzt `nativeButton={false}`, da base-ui sonst ein natives <button> erwartet.
 */
export function LinkButton({
  to,
  children,
  ...props
}: { to: LinkProps["to"] } & Omit<ButtonProps, "render" | "nativeButton">) {
  return (
    <Button nativeButton={false} render={<Link to={to} />} {...props}>
      {children}
    </Button>
  )
}
