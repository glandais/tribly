import * as zod from 'zod'

/**
 * Download a prepared data export archive using the token from the notification email.
 * @summary Download a personal data export
 */
export const DownloadDataExportParams = zod.object({
  token: zod.string().describe('Download token from the notification email'),
})

export const DownloadDataExportResponse = zod.unknown()

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
 * Queue a GDPR export of the current user's data. The archive is built in the background and a download link is emailed when it is ready. Limited to one export per hour.
 * @summary Request a personal data export
 */
export const RequestExportResponse = zod
  .object({
    id: zod.string().describe('Export job identifier'),
    status: zod
      .enum(['PENDING', 'PROCESSING', 'READY', 'FAILED', 'EXPIRED'])
      .describe('Current status'),
    requestedAt: zod.iso.datetime({ offset: true }).describe('When the export was requested'),
    completedAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the export finished building'),
    expiresAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the download link stops working'),
    fileSize: zod.number().optional().describe('Size of the archive in bytes'),
  })
  .describe('Status of a personal data export request')

/**
 * Status of the current user's most recent export request, if any.
 * @summary Get the latest data export
 */
export const GetLatestExportResponse = zod
  .object({
    id: zod.string().describe('Export job identifier'),
    status: zod
      .enum(['PENDING', 'PROCESSING', 'READY', 'FAILED', 'EXPIRED'])
      .describe('Current status'),
    requestedAt: zod.iso.datetime({ offset: true }).describe('When the export was requested'),
    completedAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the export finished building'),
    expiresAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the download link stops working'),
    fileSize: zod.number().optional().describe('Size of the archive in bytes'),
  })
  .describe('Status of a personal data export request')

/**
 * Status of one of the current user's export requests.
 * @summary Get a data export
 */
export const GetExportParams = zod.object({
  exportId: zod.string().describe('Export job identifier'),
})

export const GetExportResponse = zod
  .object({
    id: zod.string().describe('Export job identifier'),
    status: zod
      .enum(['PENDING', 'PROCESSING', 'READY', 'FAILED', 'EXPIRED'])
      .describe('Current status'),
    requestedAt: zod.iso.datetime({ offset: true }).describe('When the export was requested'),
    completedAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the export finished building'),
    expiresAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('When the download link stops working'),
    fileSize: zod.number().optional().describe('Size of the archive in bytes'),
  })
  .describe('Status of a personal data export request')

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
