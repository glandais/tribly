import * as zod from 'zod'

/**
 * Update the current user's profile
 * @summary Update current user
 */
export const updateMeBodyDisplayNameMax = 200

export const UpdateMeBody = zod
  .object({
    displayName: zod
      .string()
      .min(1)
      .max(updateMeBodyDisplayNameMax)
      .optional()
      .describe('User display name'),
    unitSystem: zod.enum(['METRIC', 'IMPERIAL']).optional().describe('Preferred unit system'),
  })
  .describe('User profile update request')

export const UpdateMeResponse = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    email: zod.string().describe('User email address'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Account creation timestamp'),
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
            linkedAt: zod.iso.datetime({ offset: true }).describe('When the identity was linked'),
          })
          .describe('A linked external identity (e.g. Strava)')
      )
      .optional()
      .describe('Linked external identities (e.g. Strava)'),
  })
  .describe('User profile data')

/**
 * Get the current authenticated user's profile.
 * @summary Get current user
 */
export const GetMeResponse = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    email: zod.string().describe('User email address'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Account creation timestamp'),
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
            linkedAt: zod.iso.datetime({ offset: true }).describe('When the identity was linked'),
          })
          .describe('A linked external identity (e.g. Strava)')
      )
      .optional()
      .describe('Linked external identities (e.g. Strava)'),
  })
  .describe('User profile data')

/**
 * Delete the current user's account
 * @summary Delete current user
 */
export const DeleteCurrentUserResponse = zod.void()

/**
 * Upload a new avatar image for the current user. Image will be resized to 256x256.
 * @summary Upload user avatar
 */
export const UploadAvatarBody = zod.object({
  file: zod.instanceof(File).optional(),
})

export const UploadAvatarResponse = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    email: zod.string().describe('User email address'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Account creation timestamp'),
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
            linkedAt: zod.iso.datetime({ offset: true }).describe('When the identity was linked'),
          })
          .describe('A linked external identity (e.g. Strava)')
      )
      .optional()
      .describe('Linked external identities (e.g. Strava)'),
  })
  .describe('User profile data')

/**
 * Remove the current user's avatar
 * @summary Delete user avatar
 */
export const DeleteAvatarResponse = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    email: zod.string().describe('User email address'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Account creation timestamp'),
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
            linkedAt: zod.iso.datetime({ offset: true }).describe('When the identity was linked'),
          })
          .describe('A linked external identity (e.g. Strava)')
      )
      .optional()
      .describe('Linked external identities (e.g. Strava)'),
  })
  .describe('User profile data')

/**
 * Search users by display name
 * @summary Search users
 */
export const searchUsersQueryLimitDefault = 10

export const SearchUsersQueryParams = zod.object({
  limit: zod.number().default(searchUsersQueryLimitDefault).describe('Maximum results (max 20)'),
  q: zod.string().optional().describe('Search query'),
})

export const SearchUsersResponseItem = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
  })
  .describe('Public user information (limited fields)')
export const SearchUsersResponse = zod.array(SearchUsersResponseItem)
