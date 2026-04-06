/**
 * Semantic badge color system for consistent visual hierarchy across cards.
 * Colors are chosen to be distinguishable and meaningful.
 */

// Entity type colors (categorical, vibrant)
export const TYPE_COLORS = {
  RIDE: 'blue',
  POST: 'grape',
  TRIP: 'teal',
  SALE: 'green',
  RENTAL: 'indigo',
  WANTED: 'orange',
} as const

// Status colors (semantic)
export const STATUS_COLORS = {
  DRAFT: 'gray',
  PUBLISHED: 'green',
  CANCELLED: 'red',
} as const

// Role colors (hierarchical)
export const ROLE_COLORS = {
  ADMIN: 'grape',
  ORGANIZER: 'blue',
  MEMBER: 'gray',
} as const

// Surface type colors (terrain-inspired)
export const SURFACE_COLORS = {
  ROAD: 'dark',
  GRAVEL: 'orange',
  MTB: 'green',
  MIXED: 'teal',
} as const

// Visibility colors
export const VISIBILITY_COLORS = {
  PUBLIC: 'blue',
  PUBLIC_UNLISTED: 'orange',
  TEAM: 'gray',
} as const

// Consolidated badge colors object for convenience
export const BADGE_COLORS = {
  type: TYPE_COLORS,
  status: STATUS_COLORS,
  role: ROLE_COLORS,
  surface: SURFACE_COLORS,
  visibility: VISIBILITY_COLORS,
} as const

// Type helpers for using the colors
export type TypeColor = keyof typeof TYPE_COLORS
export type StatusColor = keyof typeof STATUS_COLORS
export type RoleColor = keyof typeof ROLE_COLORS
export type SurfaceColor = keyof typeof SURFACE_COLORS
export type VisibilityColor = keyof typeof VISIBILITY_COLORS
