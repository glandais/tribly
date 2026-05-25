import type { DeviceRideDto } from './deviceRideDto.ts'
import type { DeviceRouteDto } from './deviceRouteDto.ts'

/**
 * Response containing rides and routes for device applications
 */
export interface DeviceRoutesResponse {
  /** Upcoming rides with route entries */
  rides: DeviceRideDto[]
  /** Latest standalone routes */
  routes: DeviceRouteDto[]
}
