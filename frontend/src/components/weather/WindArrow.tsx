import { IconArrowUp } from '@tabler/icons-react'
import { relativeWindColor, windArrowRotation } from './weatherDisplay'

interface WindArrowProps {
  /** Where the wind blows towards, relative to the direction of travel: 0 = pushing, 180 = in the face. */
  angle: number
  relativeWind: string | undefined
  /** Spoken description, e.g. « Vent de face, 18 km/h ». */
  label: string
  size?: number
}

/**
 * The wind as the rider meets it. Drawn pointing forward (up, the direction of travel), then
 * turned by `relativeWindAngle`: an arrow pointing down blows in the face. Never colour alone —
 * the caller always writes the label next to it (docs/plans/2026-10-05-weather.md §1).
 */
export function WindArrow({ angle, relativeWind, label, size = 18 }: WindArrowProps) {
  return (
    <IconArrowUp
      size={size}
      stroke={2.5}
      role="img"
      aria-label={label}
      title={label}
      color={`var(--mantine-color-${relativeWindColor(relativeWind)}-text)`}
      style={{ transform: `rotate(${windArrowRotation(angle)}deg)`, flexShrink: 0 }}
    />
  )
}
