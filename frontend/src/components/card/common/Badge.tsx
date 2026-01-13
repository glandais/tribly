import { ReactNode } from 'react'
import { Badge as MantineBadge } from '@mantine/core'

interface BadgeProps {
  variant?: 'blue' | 'green' | 'red' | 'yellow' | 'gray' | 'primary' | 'purple' | 'pink' | 'orange'
  children: ReactNode
  icon?: ReactNode
}

const variantColors: Record<string, string> = {
  blue: 'blue',
  green: 'green',
  red: 'red',
  yellow: 'yellow',
  gray: 'gray',
  primary: 'primary',
  purple: 'grape',
  pink: 'pink',
  orange: 'orange',
}

export function Badge({ variant = 'gray', children, icon }: BadgeProps) {
  return (
    <MantineBadge color={variantColors[variant]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}
