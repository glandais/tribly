import * as zod from 'zod'

/**
 * Get list of GPS service types configured for this domain
 * @summary Get available GPS services
 */
export const GetAvailableServicesResponseItem = zod.enum(['HAMMERHEAD', 'GARMIN'])
export const GetAvailableServicesResponse = zod.array(GetAvailableServicesResponseItem)

/**
 * Handles OAuth callback from GPS service and redirects to frontend
 * @summary OAuth callback
 */
export const HandleCallbackParams = zod.object({
  serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('GPS service type'),
})

export const HandleCallbackQueryParams = zod.object({
  code: zod.string().optional(),
  error: zod.string().optional(),
  state: zod.string().optional(),
})

export const HandleCallbackResponse = zod.void()

/**
 * Get the OAuth authorization URL to connect a GPS service
 * @summary Get OAuth authorization URL
 */
export const GetConnectUrlParams = zod.object({
  serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('GPS service type'),
})

export const GetConnectUrlResponse = zod
  .object({
    authorizationUrl: zod.string().describe('URL to redirect user for OAuth authorization'),
  })
  .describe('OAuth authorization URL response')

/**
 * Disconnect a connected GPS service
 * @summary Disconnect GPS service
 */
export const DisconnectParams = zod.object({
  serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('GPS service type'),
})

export const DisconnectResponse = zod.void()

/**
 * Upload a route to a connected GPS service
 * @summary Upload route to GPS service
 */
export const UploadRouteParams = zod.object({
  routeSlug: zod.string().describe('Route URL slug'),
  serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('GPS service type'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const UploadRouteResponse = zod
  .object({
    success: zod.boolean().describe('Whether the upload was successful'),
    message: zod.string().optional().describe('Error message if upload failed'),
    externalRouteId: zod.string().optional().describe('External route ID on the GPS service'),
  })
  .describe('Result of uploading a route to a GPS service')
