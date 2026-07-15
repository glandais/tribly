/**
 * Climb part information
 */
export interface ClimbPartDto {
  /** Start distance from route start in meters */
  startDistance: number
  /** End distance from route start in meters */
  endDistance: number
  /** Elevation gain in meters */
  elevationGain: number
  /** Gradient percentage */
  grade: number
}
