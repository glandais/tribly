import type { RouteUsageDto } from './routeUsageDto.ts'

/**
 * Rides and trips that use a route
 */
export interface RouteUsagesResponse {
  /** Usages of the route, most recent first */
  usages: RouteUsageDto[]
}
