import type { RouteRequest } from './routeRequest'

export type UpdateRouteBody = {
  route?: RouteRequest
  gpxFile?: Blob
}
