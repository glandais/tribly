import { ReactNode, MouseEvent } from 'react'
import { Button, MantineColor } from '@mantine/core'

interface CardActionProps {
  label: string
  icon?: ReactNode
  onClick: (e: MouseEvent<HTMLButtonElement>) => void
  variant?: 'filled' | 'light' | 'subtle'
  color?: MantineColor
  loading?: boolean
  disabled?: boolean
}

/**
 * CardAction component - a reusable CTA button for cards.
 * Stops event propagation so the card link doesn't navigate when clicking the action.
 * Position it in the bottom-right of card content for consistent placement.
 */
export function CardAction({
  label,
  icon,
  onClick,
  variant = 'light',
  color = 'primary',
  loading = false,
  disabled = false,
}: CardActionProps) {
  const handleClick = (e: MouseEvent<HTMLButtonElement>) => {
    e.preventDefault()
    e.stopPropagation()
    onClick(e)
  }

  return (
    <Button
      size="sm"
      variant={variant}
      color={color}
      leftSection={icon}
      onClick={handleClick}
      loading={loading}
      disabled={disabled}
    >
      {label}
    </Button>
  )
}
