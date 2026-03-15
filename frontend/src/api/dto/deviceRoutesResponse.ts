import type { DeviceRideDto } from './deviceRideDto'
import type { DeviceRouteDto } from './deviceRouteDto'

/**
 * Response containing rides and routes for device applications
 */
export interface DeviceRoutesResponse {
  /** Upcoming rides with route entries */
  rides: DeviceRideDto[]
  /** Latest standalone routes */
  routes: DeviceRouteDto[]
}
