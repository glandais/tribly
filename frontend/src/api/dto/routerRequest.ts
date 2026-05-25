import type { GeoPoint } from './geoPoint.ts'
import type { RouterProfile } from './routerProfile.ts'

export interface RouterRequest {
  from: GeoPoint
  to: GeoPoint
  profile: RouterProfile
}
