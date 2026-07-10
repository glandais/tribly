import type { Hilliness } from './hilliness.ts'
import type { MinRole } from './minRole.ts'
import type { NearType } from './nearType.ts'
import type { RouteSortBy } from './routeSortBy.ts'
import type { SortDirection } from './sortDirection.ts'
import type { SurfaceType } from './surfaceType.ts'
import type { WindDirection } from './windDirection.ts'

export type ListAllRoutesParams = {
  /**
   * Hilliness preset (FLAT, HILLY, MOUNTAINOUS)
   */
  hilliness?: Hilliness
  /**
   * Maximum distance in meters
   */
  maxDistance?: number
  /**
   * Maximum elevation gain in meters
   */
  maxElevationGain?: number
  /**
   * Minimum distance in meters
   */
  minDistance?: number
  /**
   * Minimum elevation gain in meters
   */
  minElevationGain?: number
  /**
   * Only routes from teams where the user has at least this role. Yields nothing for an anonymous visitor.
   */
  minRole?: MinRole
  /**
   * Latitude for proximity search
   */
  nearLat?: number
  /**
   * Longitude for proximity search
   */
  nearLon?: number
  /**
   * Search radius in meters (default: 25000)
   */
  nearRadius?: number
  /**
   * Search near START, END, or START_OR_END (default)
   */
  nearType?: NearType
  /**
   * Page number (0-indexed)
   */
  page?: number
  /**
   * Search by name/markdown
   */
  search?: string
  /**
   * Page size
   */
  size?: number
  /**
   * Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)
   */
  sortBy?: RouteSortBy
  /**
   * Sort direction (ASC, DESC)
   */
  sortDir?: SortDirection
  /**
   * Filter by surface type
   */
  surfaceType?: SurfaceType
  /**
   * Filter by wind direction
   */
  windDirection?: WindDirection
}
