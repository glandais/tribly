import * as zod from 'zod'

/**
 * Handles the Strava OAuth redirect and hands off to the frontend
 * @summary Strava OAuth callback
 */
export const StravaCallbackQueryParams = zod.object({
  code: zod.string().optional(),
  error: zod.string().optional(),
  state: zod.string().optional(),
})

export const StravaCallbackResponse = zod.void()

/**
 * Get the Strava OAuth authorization URL to link Strava to the current account
 * @summary Get Strava link URL
 */
export const GetStravaConnectUrlResponse = zod
  .object({
    authorizationUrl: zod.string().describe('URL to redirect the user to for Strava authorization'),
  })
  .describe('Strava OAuth authorization URL response')

/**
 * Remove the Strava identity from the account
 * @summary Unlink Strava
 */
export const UnlinkStravaResponse = zod.void()

/**
 * Get the Strava OAuth authorization URL to log in / recover an account
 * @summary Get Strava login URL
 */
export const GetStravaLoginUrlResponse = zod
  .object({
    authorizationUrl: zod.string().describe('URL to redirect the user to for Strava authorization'),
  })
  .describe('Strava OAuth authorization URL response')

/**
 * Exchange the one-time code from the Strava callback for an authenticated session
 * @summary Exchange Strava login code for a session
 */
export const CreateStravaSessionHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const createStravaSessionBodyCodeRegExp = new RegExp('\\S')

export const CreateStravaSessionBody = zod
  .object({
    code: zod
      .string()
      .regex(createStravaSessionBodyCodeRegExp)
      .describe('One-time login code from the Strava callback'),
  })
  .describe('Exchange a one-time Strava login code for a session')

export const CreateStravaSessionResponse = zod
  .object({
    accessToken: zod.string().optional().describe('JWT access token'),
    expiresIn: zod.number().optional().describe('Token expiry in seconds'),
    user: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        email: zod.string().describe('User email address'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
        createdAt: zod.iso
          .datetime({ offset: true })
          .optional()
          .describe('Account creation timestamp'),
        unitSystem: zod
          .enum(['METRIC', 'IMPERIAL'])
          .optional()
          .describe('Preferred unit system (metric or imperial)'),
        platformRole: zod
          .enum(['PLATFORM_ADMIN'])
          .optional()
          .describe('Platform role (null if regular user)'),
        emailVerified: zod.boolean().describe("Whether the account's email has been verified"),
        requiresEmail: zod
          .boolean()
          .describe(
            'True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address)'
          ),
        connectedServices: zod
          .array(
            zod
              .object({
                serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('Service type identifier'),
                displayName: zod.string().describe('Display name of the service'),
                connectedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the service was connected'),
              })
              .describe('GPS service connection information')
          )
          .optional()
          .describe('Connected GPS services'),
        socialIdentities: zod
          .array(
            zod
              .object({
                provider: zod.enum(['STRAVA']).describe('Provider identifier'),
                displayName: zod.string().describe('Display name of the provider'),
                linkedAt: zod.iso
                  .datetime({ offset: true })
                  .describe('When the identity was linked'),
              })
              .describe('A linked external identity (e.g. Strava)')
          )
          .optional()
          .describe('Linked external identities (e.g. Strava)'),
      })
      .optional()
      .describe('Authenticated user'),
    refreshToken: zod.string().optional().describe('Refresh token (for mobile clients)'),
  })
  .describe('Authentication response')
