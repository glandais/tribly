import * as zod from 'zod'

/**
 * Get routes for authenticated user. Prioritizes routes from upcoming rides, then latest routes from user's teams.
 * @summary List routes for user
 */
export const DeviceListRoutesQueryParams = zod.object({
  lat: zod.number().optional().describe("User's current latitude (for proximity sorting)"),
  lon: zod.number().optional().describe("User's current longitude (for proximity sorting)"),
})

export const DeviceListRoutesResponse = zod
  .object({
    rides: zod
      .array(
        zod
          .object({
            teamSlug: zod.string().describe('Team slug'),
            rideSlug: zod.string().describe('Ride slug'),
            rideName: zod.string().describe('Ride name'),
            startDateTime: zod.iso
              .datetime({ offset: true })
              .optional()
              .describe('Start date\/time'),
            entries: zod
              .array(
                zod
                  .object({
                    routeSlug: zod.string().describe('Route slug'),
                    routeName: zod.string().describe('Route name'),
                    groupName: zod
                      .string()
                      .optional()
                      .describe('Group name (null for ride-level route)'),
                    distance: zod.number().describe('Distance in meters'),
                    elevationGain: zod.number().describe('Elevation gain in meters'),
                    startLat: zod.number().optional().describe('Start latitude'),
                    startLon: zod.number().optional().describe('Start longitude'),
                  })
                  .describe('Route entry within a ride for device applications')
              )
              .describe('Route entries for this ride'),
          })
          .describe('Ride information for device applications')
      )
      .describe('Upcoming rides with route entries'),
    routes: zod
      .array(
        zod
          .object({
            teamSlug: zod.string().describe('Team slug'),
            routeSlug: zod.string().describe('Route slug'),
            routeName: zod.string().describe('Route name'),
            distance: zod.number().describe('Distance in meters'),
            elevationGain: zod.number().describe('Elevation gain in meters'),
            startLat: zod.number().optional().describe('Start latitude'),
            startLon: zod.number().optional().describe('Start longitude'),
          })
          .describe('Standalone route information for device applications')
      )
      .describe('Latest standalone routes'),
  })
  .describe('Response containing rides and routes for device applications')

/**
 * Download route as FIT file for GPS devices
 * @summary Download FIT file
 */
export const DeviceDownloadFitParams = zod.object({
  routeSlug: zod.string().describe('Route URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Download route as GPX file for GPS devices
 * @summary Download GPX file
 */
export const DeviceDownloadGpxParams = zod.object({
  routeSlug: zod.string().describe('Route URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Upload route to a cloud GPS service (e.g., Hammerhead, Garmin Connect)
 * @summary Sync route to cloud service
 */
export const DeviceSyncRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team slug'),
})

export const DeviceSyncRouteQueryParams = zod.object({
  type: zod.string().describe('GPS service type (hammerhead, garmin)'),
})

export const DeviceSyncRouteResponse = zod
  .object({
    success: zod.boolean().describe('Whether the upload was successful'),
    message: zod.string().optional().describe('Error message if upload failed'),
    externalRouteId: zod.string().optional().describe('External route ID on the GPS service'),
  })
  .describe('Result of uploading a route to a GPS service')
