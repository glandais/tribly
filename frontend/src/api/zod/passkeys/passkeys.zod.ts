import * as zod from 'zod'

/**
 * List all passkeys registered for the current user
 * @summary List passkeys
 */
export const ListPasskeysResponseItem = zod
  .object({
    id: zod.string().optional().describe('Passkey ID'),
    credentialId: zod.string().optional().describe('Credential ID (base64url)'),
    deviceName: zod.string().optional().describe('Device name'),
    transports: zod.array(zod.string()).optional().describe('Transport methods'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Created timestamp'),
    lastUsedAt: zod.iso.datetime({ offset: true }).optional().describe('Last used timestamp'),
  })
  .describe('Passkey information')
export const ListPasskeysResponse = zod.array(ListPasskeysResponseItem)

/**
 * Authenticate using a passkey
 * @summary Authenticate with passkey
 */
export const AuthenticateHeader = zod.object({
  'X-Forwarded-For': zod.string().optional(),
  'X-Real-IP': zod.string().optional(),
})

export const AuthenticateBody = zod.record(zod.string(), zod.unknown())

export const AuthenticateResponse = zod
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
        theme: zod
          .enum(['SYSTEM', 'LIGHT', 'DARK'])
          .optional()
          .describe(
            'Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device.'
          ),
        language: zod
          .string()
          .optional()
          .describe(
            'Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain.'
          ),
        contactableByMembers: zod
          .boolean()
          .describe(
            'Whether team members may reach this user through the classified-ad relay. True unless they explicitly opted out, so an account that predates the preference is contactable.'
          ),
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

/**
 * Get WebAuthn options for authenticating with a passkey
 * @summary Get authentication options
 */
export const getAuthenticationOptionsBodyEmailMax = 250

export const GetAuthenticationOptionsBody = zod
  .object({
    email: zod
      .string()
      .max(getAuthenticationOptionsBodyEmailMax)
      .optional()
      .describe('Optional email to filter passkeys'),
  })
  .describe('Request for passkey authentication options')

export const GetAuthenticationOptionsResponse = zod.unknown()

/**
 * Verify and register a new passkey for the current user
 * @summary Register passkey
 */
export const RegisterPasskeyQueryParams = zod.object({
  deviceName: zod.string().optional(),
})

export const RegisterPasskeyBody = zod.record(zod.string(), zod.unknown())

export const RegisterPasskeyResponse = zod
  .object({
    id: zod.string().optional().describe('Passkey ID'),
    credentialId: zod.string().optional().describe('Credential ID (base64url)'),
    deviceName: zod.string().optional().describe('Device name'),
    transports: zod.array(zod.string()).optional().describe('Transport methods'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Created timestamp'),
    lastUsedAt: zod.iso.datetime({ offset: true }).optional().describe('Last used timestamp'),
  })
  .describe('Passkey information')

/**
 * Get WebAuthn options for registering a new passkey
 * @summary Get registration options
 */
export const GetRegistrationOptionsResponse = zod.unknown()

/**
 * Delete a passkey
 * @summary Delete passkey
 */
export const DeletePasskeyParams = zod.object({
  id: zod.string(),
})

export const DeletePasskeyResponse = zod.void()
