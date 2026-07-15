import type { ClimbCategory } from './climbCategory.ts'
import type { ClimbPartDto } from './climbPartDto.ts'

/**
 * Climb segment information
 */
export interface ClimbDto {
  /** Start distance from route start in meters */
  startDistance: number
  /** End distance from route start in meters */
  endDistance: number
  /** Elevation gain in meters */
  elevationGain: number
  /** Average gradient percentage */
  averageGradient: number
  /** Maximum gradient percentage */
  maxGradient: number
  /** Climb category (HC, 1, 2, 3, 4) */
  category?: ClimbCategory
  /** Gradient segments making up the climb */
  parts: ClimbPartDto[]
}
