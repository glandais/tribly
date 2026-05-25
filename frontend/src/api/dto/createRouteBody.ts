import type { RouteRequest } from './routeRequest.ts'

export type CreateRouteBody = {
  route?: RouteRequest
  gpxFile?: Blob
}
