import * as zod from 'zod'

/**
 * Get authenticated user's status including connected GPS services
 * @summary Get current user status
 */
export const DeviceGetMeResponse = zod
  .object({
    connectedGpsServices: zod
      .array(zod.enum(['HAMMERHEAD', 'GARMIN']))
      .describe('List of connected GPS services'),
  })
  .describe("Response containing user's device connection status")
