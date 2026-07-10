import type { Instant } from './instant.ts'

/**
 * Summary of an analysed GPX file, for listing
 */
export interface GpxPreviewSummaryDto {
  /** Public identifier used in URLs */
  id: string
  /** Track name */
  name: string
  /** Distance in meters */
  distance: number
  /** Total elevation gain in meters */
  elevationGain: number
  /** Total elevation loss in meters */
  elevationLoss: number
  /** Elevation gain per kilometer */
  hilliness: number
  /** Creation timestamp */
  createdAt: Instant
}
