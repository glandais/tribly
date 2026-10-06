import type { CompassPoint } from './compassPoint.ts'

/**
 * Wind at a place and an hour, 10 m above ground
 */
export interface WindDto {
  /** Mean wind speed, km/h */
  speed: number
  /** Gusts, km/h. Absent when the model gives none. */
  gusts?: number
  /** Direction the wind comes FROM, degrees clockwise from north (0 = from the north, 90 = from the east) */
  direction: number
  /** direction on the eight-point rose, still the direction the wind comes FROM */
  compass: CompassPoint
}
