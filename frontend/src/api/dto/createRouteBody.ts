import type { RouteRequest } from './routeRequest'

export type CreateRouteBody = {
  route?: RouteRequest
  gpxFile?: Blob
}
