/**
 * Standalone route information for device applications
 */
export interface DeviceRouteDto {
  /** Team slug */
  teamSlug: string
  /** Route slug */
  routeSlug: string
  /** Route name */
  routeName: string
  /** Distance in meters */
  distance: number
  /** Elevation gain in meters */
  elevationGain: number
  /** Start latitude */
  startLat?: number
  /** Start longitude */
  startLon?: number
}
