import type { RouteDto } from './routeDto.ts'

/**
 * Paginated route list response
 */
export interface RouteListResponse {
  /** List of routes */
  routes: RouteDto[]
  /** Total number of routes */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
