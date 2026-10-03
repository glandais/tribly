import type { ReactNode } from 'react'
import { Box, type MantineColor } from '@mantine/core'

interface VisualFrameProps {
  color: MantineColor
  children: ReactNode
}

/**
 * The tinted panel every illustration of the features page sits in. The illustrations are
 * decorative — fictional demo content, the section's text already says what they show — so the
 * whole frame is hidden from assistive technologies, and nothing inside it is focusable (buttons
 * are drawn as `span`s, see `DemoButton`).
 */
export function VisualFrame({ color, children }: VisualFrameProps) {
  return (
    <Box
      aria-hidden
      p={{ base: 'md', sm: 'xl' }}
      style={{
        borderRadius: 'var(--mantine-radius-xl)',
        border: '1px solid var(--mantine-color-default-border)',
        backgroundColor: `var(--mantine-color-${color}-light)`,
      }}
    >
      {children}
    </Box>
  )
}
