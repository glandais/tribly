import { ReactNode } from 'react'
import { Badge as MantineBadge } from '@mantine/core'
import {
  TYPE_COLORS,
  STATUS_COLORS,
  ROLE_COLORS,
  SURFACE_COLORS,
  VISIBILITY_COLORS,
} from './badgeColors'

// Direct color variants
type ColorVariant =
  | 'blue'
  | 'green'
  | 'red'
  | 'yellow'
  | 'gray'
  | 'primary'
  | 'purple'
  | 'pink'
  | 'orange'
  | 'teal'
  | 'indigo'
  | 'dark'
  | 'grape'

interface BadgeProps {
  variant?: ColorVariant
  children: ReactNode
  icon?: ReactNode
}

const variantColors: Record<ColorVariant, string> = {
  blue: 'blue',
  green: 'green',
  red: 'red',
  yellow: 'yellow',
  gray: 'gray',
  primary: 'primary',
  purple: 'grape',
  pink: 'pink',
  orange: 'orange',
  teal: 'teal',
  indigo: 'indigo',
  dark: 'dark',
  grape: 'grape',
}

export function Badge({ variant = 'gray', children, icon }: BadgeProps) {
  return (
    <MantineBadge color={variantColors[variant]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}

// Semantic badge components for common use cases

interface TypeBadgeProps {
  type: keyof typeof TYPE_COLORS
  children: ReactNode
  icon?: ReactNode
}

export function TypeBadge({ type, children, icon }: TypeBadgeProps) {
  return (
    <MantineBadge color={TYPE_COLORS[type]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}

interface StatusBadgeProps {
  status: keyof typeof STATUS_COLORS
  children: ReactNode
  icon?: ReactNode
}

export function StatusBadge({ status, children, icon }: StatusBadgeProps) {
  return (
    <MantineBadge color={STATUS_COLORS[status]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}

interface RoleBadgeProps {
  role: keyof typeof ROLE_COLORS
  children: ReactNode
  icon?: ReactNode
}

export function RoleBadge({ role, children, icon }: RoleBadgeProps) {
  return (
    <MantineBadge color={ROLE_COLORS[role]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}

interface SurfaceBadgeProps {
  surface: keyof typeof SURFACE_COLORS
  children: ReactNode
  icon?: ReactNode
}

export function SurfaceBadge({ surface, children, icon }: SurfaceBadgeProps) {
  return (
    <MantineBadge color={SURFACE_COLORS[surface]} variant="light" size="sm" leftSection={icon}>
      {children}
    </MantineBadge>
  )
}

interface VisibilityBadgeSemanticProps {
  visibility: keyof typeof VISIBILITY_COLORS
  children: ReactNode
  icon?: ReactNode
}

export function VisibilityBadgeSemantic({
  visibility,
  children,
  icon,
}: VisibilityBadgeSemanticProps) {
  return (
    <MantineBadge
      color={VISIBILITY_COLORS[visibility]}
      variant="light"
      size="sm"
      leftSection={icon}
    >
      {children}
    </MantineBadge>
  )
}
