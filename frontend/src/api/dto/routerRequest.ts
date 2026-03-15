import type { GeoPoint } from './geoPoint'
import type { RouterProfile } from './routerProfile'

export interface RouterRequest {
  from: GeoPoint
  to: GeoPoint
  profile: RouterProfile
}
