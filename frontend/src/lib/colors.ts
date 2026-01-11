/**
 * Transparent background colors for overlays that adapt to color scheme.
 * Used for map tooltips, elevation charts, floating UI elements, etc.
 */

export const OVERLAY_BG = {
  light: 'rgba(255, 255, 255, 0.9)',
  dark: 'rgba(36, 36, 36, 0.9)',
} as const

export const OVERLAY_BG_SOLID = {
  light: 'rgba(255, 255, 255, 0.95)',
  dark: 'rgba(36, 36, 36, 0.95)',
} as const

/**
 * Get overlay background color based on color scheme
 */
export function getOverlayBg(colorScheme: 'light' | 'dark', solid = false): string {
  const colors = solid ? OVERLAY_BG_SOLID : OVERLAY_BG
  return colors[colorScheme]
}
