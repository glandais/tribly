import type { ReactNode } from 'react'
import { Button, type ButtonProps } from '@mantine/core'

/**
 * A button as drawn in an illustration: the look of a Mantine `Button`, rendered as a `span` so it
 * is neither focusable nor announced as a control that does nothing.
 */
export function DemoButton({ children, ...props }: ButtonProps & { children: ReactNode }) {
  return (
    <Button component="span" size="xs" {...props}>
      {children}
    </Button>
  )
}
