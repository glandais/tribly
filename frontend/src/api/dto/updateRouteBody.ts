import type { RouteRequest } from './routeRequest.ts'

export type UpdateRouteBody = {
  route?: RouteRequest
  gpxFile?: Blob
}
