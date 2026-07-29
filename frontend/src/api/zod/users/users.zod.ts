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
            serviceType: zod
              .enum(['HAMMERHEAD', 'GARMIN', 'WAHOO'])
              .describe('Service type identifier'),
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
            serviceType: zod
              .enum(['HAMMERHEAD', 'GARMIN', 'WAHOO'])
              .describe('Service type identifier'),
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
            serviceType: zod
              .enum(['HAMMERHEAD', 'GARMIN', 'WAHOO'])
              .describe('Service type identifier'),
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
            serviceType: zod
              .enum(['HAMMERHEAD', 'GARMIN', 'WAHOO'])
              .describe('Service type identifier'),
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
 * The rides and trips the current user is registered to, soonest first. Only publications the user may still see are returned: leaving a team removes its outings from this list.
 * @summary List my participations
 */
export const listMyParticipationsQueryPageDefault = 0
export const listMyParticipationsQuerySizeDefault = 20

export const ListMyParticipationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  page: zod.number().default(listMyParticipationsQueryPageDefault).describe('Page number'),
  size: zod.number().default(listMyParticipationsQuerySizeDefault).describe('Page size'),
  status: zod
    .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
    .optional()
    .describe('Only publications with this status'),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
})

export const ListMyParticipationsResponse = zod
  .object({
    publications: zod
      .array(
        zod
          .union([
            zod
              .object({
                type: zod.enum(['RIDE']),
                team: zod
                  .object({
                    id: zod.string().describe('Team ID (TSID)'),
                    name: zod.string().describe('Team name'),
                    slug: zod.string().describe('Team URL slug'),
                    visibility: zod
                      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                      .describe('Whether the team is public'),
                  })
                  .describe('Team'),
                id: zod.string().describe('Publication ID (TSID)'),
                slug: zod.string().describe('Publication URL slug'),
                name: zod.string().describe('Publication name'),
                media: zod
                  .object({
                    markdown: zod.string().describe('Markdown'),
                    assets: zod
                      .object({
                        logo: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Logo'),
                        images: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Images'),
                        attachments: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Attachments'),
                        originalGpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Original GPX'),
                        gpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('GPX'),
                        fit: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('FIT'),
                        thumbnailLight: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Light thumbnail'),
                        thumbnailDark: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Dark thumbnail'),
                      })
                      .describe('Assets'),
                  })
                  .describe('Publication media'),
                excerpt: zod
                  .string()
                  .optional()
                  .describe(
                    "Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter."
                  ),
                dateTime: zod.iso.datetime({ offset: true }).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Publication timestamp'),
                createdAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Creation timestamp'),
                routeSlug: zod.string().optional().describe('Route slug'),
                participantCount: zod.number().describe('Number of participants'),
                groupCount: zod.number().describe('Number of groups'),
                groups: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Group ID (TSID)'),
                        name: zod.string().describe('Group name'),
                        time: zod.string().optional(),
                        routeSlug: zod.string().optional().describe('Route slug'),
                        averageSpeed: zod.number().optional().describe('Average speed in km\/h'),
                        maxParticipants: zod.number().optional().describe('Maximum participants'),
                        countParticipants: zod.number().describe('Current number of participants'),
                        participants: zod
                          .array(
                            zod
                              .object({
                                id: zod.string().describe('User ID (TSID)'),
                                displayName: zod.string().describe('User display name'),
                                avatarUrl: zod.string().optional().describe('User avatar URL'),
                              })
                              .describe('Public user information (limited fields)')
                          )
                          .describe('Participants, empty if not access'),
                        sortOrder: zod.number().describe('Sort order'),
                        registered: zod
                          .boolean()
                          .describe(
                            'Whether the current user is registered in THIS group. False if anonymous.'
                          ),
                        full: zod
                          .boolean()
                          .describe(
                            'Whether the group has reached maxParticipants. False when maxParticipants is not set.'
                          ),
                        distance: zod
                          .number()
                          .optional()
                          .describe('Distance in meters of the group route, if it has one'),
                        elevationGain: zod
                          .number()
                          .optional()
                          .describe(
                            'Total elevation gain in meters of the group route, if it has one'
                          ),
                        leader: zod
                          .object({
                            id: zod.string().describe('User ID (TSID)'),
                            displayName: zod.string().describe('User display name'),
                            avatarUrl: zod.string().optional().describe('User avatar URL'),
                          })
                          .optional()
                          .describe(
                            "The member who leads this group, when one is designated. Null means no leader was designated — render nothing rather than falling back on the ride's creator, who is the same person on every group of the ride."
                          ),
                      })
                      .describe('Ride group information')
                  )
                  .describe('Ride groups'),
                startPlace: zod
                  .object({
                    id: zod.string().describe('Place ID (TSID)'),
                    name: zod.string(),
                    address: zod.string().optional(),
                    link: zod.string().optional(),
                    startPlace: zod.boolean(),
                    endPlace: zod.boolean(),
                    geometry: zod
                      .object({
                        type: zod.enum(['Point']),
                        coordinates: zod
                          .array(zod.number())
                          .describe('Coordinates [longitude, latitude]'),
                      })
                      .optional()
                      .describe('Location coordinates [longitude, latitude]'),
                  })
                  .optional()
                  .describe('Start place'),
                endPlace: zod
                  .object({
                    id: zod.string().describe('Place ID (TSID)'),
                    name: zod.string(),
                    address: zod.string().optional(),
                    link: zod.string().optional(),
                    startPlace: zod.boolean(),
                    endPlace: zod.boolean(),
                    geometry: zod
                      .object({
                        type: zod.enum(['Point']),
                        coordinates: zod
                          .array(zod.number())
                          .describe('Coordinates [longitude, latitude]'),
                      })
                      .optional()
                      .describe('Location coordinates [longitude, latitude]'),
                  })
                  .optional()
                  .describe('End place'),
                topParticipants: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('User ID (TSID)'),
                        displayName: zod.string().describe('User display name'),
                        avatarUrl: zod.string().optional().describe('User avatar URL'),
                      })
                      .describe('Public user information (limited fields)')
                  )
                  .describe('Preview of first participants (max 5)'),
                thumbnailLightUrl: zod.string().optional().describe('Thumbnail URL (light)'),
                thumbnailDarkUrl: zod.string().optional().describe('Thumbnail URL (dark)'),
                thumbnailUrl: zod
                  .string()
                  .optional()
                  .describe(
                    'The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.'
                  ),
                deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
                registered: zod
                  .boolean()
                  .describe(
                    "Whether the current user is registered in one of this ride's groups. False if anonymous."
                  ),
                registeredGroupId: zod
                  .string()
                  .optional()
                  .describe(
                    'ID (TSID) of the group the current user joined, null if not registered'
                  ),
                full: zod
                  .boolean()
                  .describe(
                    'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
                  ),
                commentCount: zod
                  .number()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
              })
              .describe('Ride summary data'),
            zod
              .object({
                type: zod.enum(['POST']),
                team: zod
                  .object({
                    id: zod.string().describe('Team ID (TSID)'),
                    name: zod.string().describe('Team name'),
                    slug: zod.string().describe('Team URL slug'),
                    visibility: zod
                      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                      .describe('Whether the team is public'),
                  })
                  .describe('Team'),
                id: zod.string().describe('Publication ID (TSID)'),
                slug: zod.string().describe('Publication URL slug'),
                name: zod.string().describe('Publication name'),
                media: zod
                  .object({
                    markdown: zod.string().describe('Markdown'),
                    assets: zod
                      .object({
                        logo: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Logo'),
                        images: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Images'),
                        attachments: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Attachments'),
                        originalGpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Original GPX'),
                        gpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('GPX'),
                        fit: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('FIT'),
                        thumbnailLight: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Light thumbnail'),
                        thumbnailDark: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Dark thumbnail'),
                      })
                      .describe('Assets'),
                  })
                  .describe('Publication media'),
                excerpt: zod
                  .string()
                  .optional()
                  .describe(
                    "Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter."
                  ),
                thumbnailUrl: zod
                  .string()
                  .optional()
                  .describe(
                    "URL template of the post's first image, the one a card shows. Saves a compact row from carrying media.assets just to find a picture."
                  ),
                dateTime: zod.iso.datetime({ offset: true }).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Publication timestamp'),
                createdAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Creation timestamp'),
                deleted: zod.boolean().describe('Whether the post is soft-deleted'),
                commentCount: zod
                  .number()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this post — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
              })
              .describe('Post summary data'),
            zod
              .object({
                type: zod.enum(['TRIP']),
                team: zod
                  .object({
                    id: zod.string().describe('Team ID (TSID)'),
                    name: zod.string().describe('Team name'),
                    slug: zod.string().describe('Team URL slug'),
                    visibility: zod
                      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                      .describe('Whether the team is public'),
                  })
                  .describe('Team'),
                id: zod.string().describe('Publication ID (TSID)'),
                slug: zod.string().describe('Publication URL slug'),
                name: zod.string().describe('Publication name'),
                media: zod
                  .object({
                    markdown: zod.string().describe('Markdown'),
                    assets: zod
                      .object({
                        logo: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Logo'),
                        images: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Images'),
                        attachments: zod
                          .array(
                            zod.object({
                              id: zod.string().describe('ID (TSID)'),
                              fileName: zod.string().describe('Filename'),
                              contentType: zod.string().describe('Content-Type'),
                              url: zod.string().describe('url'),
                              imageUrl: zod.string().optional().describe('image template url'),
                              imageDimensions: zod
                                .object({
                                  width: zod.number().optional(),
                                  height: zod.number().optional(),
                                })
                                .optional()
                                .describe('image dimensions'),
                            })
                          )
                          .describe('Attachments'),
                        originalGpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Original GPX'),
                        gpx: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('GPX'),
                        fit: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('FIT'),
                        thumbnailLight: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Light thumbnail'),
                        thumbnailDark: zod
                          .object({
                            id: zod.string().describe('ID (TSID)'),
                            fileName: zod.string().describe('Filename'),
                            contentType: zod.string().describe('Content-Type'),
                            url: zod.string().describe('url'),
                            imageUrl: zod.string().optional().describe('image template url'),
                            imageDimensions: zod
                              .object({
                                width: zod.number().optional(),
                                height: zod.number().optional(),
                              })
                              .optional()
                              .describe('image dimensions'),
                          })
                          .optional()
                          .describe('Dark thumbnail'),
                      })
                      .describe('Assets'),
                  })
                  .describe('Publication media'),
                excerpt: zod
                  .string()
                  .optional()
                  .describe(
                    "Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter."
                  ),
                dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date\/time'),
                endDate: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe(
                    'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
                  ),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Publication timestamp'),
                createdAt: zod.iso
                  .datetime({ offset: true })
                  .optional()
                  .describe('Creation timestamp'),
                routeSlug: zod.string().optional().describe('Route slug'),
                participantCount: zod.number().describe('Number of participants'),
                stageCount: zod.number().describe('Number of stages'),
                totalDistance: zod
                  .number()
                  .optional()
                  .describe(
                    'Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero.'
                  ),
                totalElevationGain: zod
                  .number()
                  .optional()
                  .describe(
                    'Elevation gain in metres over every stage that has a route. Null when no stage has one.'
                  ),
                stages: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Stage ID (TSID)'),
                        slug: zod.string().describe('Stage slug'),
                        name: zod.string().describe('Stage name'),
                        dateTime: zod.iso.datetime({ offset: true }).describe('Stage date\/time'),
                        route: zod
                          .object({
                            id: zod.string().describe('Route ID (TSID)'),
                            slug: zod.string().describe('Route slug'),
                            team: zod
                              .object({
                                id: zod.string().describe('Team ID (TSID)'),
                                name: zod.string().describe('Team name'),
                                slug: zod.string().describe('Team URL slug'),
                                visibility: zod
                                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                                  .describe('Whether the team is public'),
                              })
                              .describe('Team'),
                            name: zod.string().describe('Route name'),
                            media: zod
                              .object({
                                markdown: zod.string().describe('Markdown'),
                                assets: zod
                                  .object({
                                    logo: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('Logo'),
                                    images: zod
                                      .array(
                                        zod.object({
                                          id: zod.string().describe('ID (TSID)'),
                                          fileName: zod.string().describe('Filename'),
                                          contentType: zod.string().describe('Content-Type'),
                                          url: zod.string().describe('url'),
                                          imageUrl: zod
                                            .string()
                                            .optional()
                                            .describe('image template url'),
                                          imageDimensions: zod
                                            .object({
                                              width: zod.number().optional(),
                                              height: zod.number().optional(),
                                            })
                                            .optional()
                                            .describe('image dimensions'),
                                        })
                                      )
                                      .describe('Images'),
                                    attachments: zod
                                      .array(
                                        zod.object({
                                          id: zod.string().describe('ID (TSID)'),
                                          fileName: zod.string().describe('Filename'),
                                          contentType: zod.string().describe('Content-Type'),
                                          url: zod.string().describe('url'),
                                          imageUrl: zod
                                            .string()
                                            .optional()
                                            .describe('image template url'),
                                          imageDimensions: zod
                                            .object({
                                              width: zod.number().optional(),
                                              height: zod.number().optional(),
                                            })
                                            .optional()
                                            .describe('image dimensions'),
                                        })
                                      )
                                      .describe('Attachments'),
                                    originalGpx: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('Original GPX'),
                                    gpx: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('GPX'),
                                    fit: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('FIT'),
                                    thumbnailLight: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('Light thumbnail'),
                                    thumbnailDark: zod
                                      .object({
                                        id: zod.string().describe('ID (TSID)'),
                                        fileName: zod.string().describe('Filename'),
                                        contentType: zod.string().describe('Content-Type'),
                                        url: zod.string().describe('url'),
                                        imageUrl: zod
                                          .string()
                                          .optional()
                                          .describe('image template url'),
                                        imageDimensions: zod
                                          .object({
                                            width: zod.number().optional(),
                                            height: zod.number().optional(),
                                          })
                                          .optional()
                                          .describe('image dimensions'),
                                      })
                                      .optional()
                                      .describe('Dark thumbnail'),
                                  })
                                  .describe('Assets'),
                              })
                              .describe('Route description'),
                            excerpt: zod
                              .string()
                              .optional()
                              .describe(
                                "Plain-text opening of the description, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the description holds no text. Lets a list row render its two lines without the description being sent at all — see the 'view' parameter."
                              ),
                            thumbnailUrl: zod
                              .string()
                              .optional()
                              .describe(
                                "URL template of the route's thumbnail, light variant if there is one, else dark. Saves a compact row from carrying media.assets just to find the map preview."
                              ),
                            distance: zod.number().describe('Distance in meters'),
                            elevationGain: zod.number().describe('Total elevation gain in meters'),
                            elevationLoss: zod.number().describe('Total elevation loss in meters'),
                            surfaceType: zod
                              .enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED'])
                              .describe('Surface type'),
                            visibility: zod
                              .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                              .describe('Whether the route is public'),
                            createdAt: zod.iso
                              .datetime({ offset: true })
                              .describe('Creation timestamp'),
                            deleted: zod.boolean().describe('Whether the route is soft-deleted'),
                            commentCount: zod
                              .number()
                              .optional()
                              .describe(
                                'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                              ),
                          })
                          .optional()
                          .describe('Route'),
                        startPlace: zod
                          .object({
                            id: zod.string().describe('Place ID (TSID)'),
                            name: zod.string(),
                            address: zod.string().optional(),
                            link: zod.string().optional(),
                            startPlace: zod.boolean(),
                            endPlace: zod.boolean(),
                            geometry: zod
                              .object({
                                type: zod.enum(['Point']),
                                coordinates: zod
                                  .array(zod.number())
                                  .describe('Coordinates [longitude, latitude]'),
                              })
                              .optional()
                              .describe('Location coordinates [longitude, latitude]'),
                          })
                          .optional()
                          .describe('Start place'),
                        endPlace: zod
                          .object({
                            id: zod.string().describe('Place ID (TSID)'),
                            name: zod.string(),
                            address: zod.string().optional(),
                            link: zod.string().optional(),
                            startPlace: zod.boolean(),
                            endPlace: zod.boolean(),
                            geometry: zod
                              .object({
                                type: zod.enum(['Point']),
                                coordinates: zod
                                  .array(zod.number())
                                  .describe('Coordinates [longitude, latitude]'),
                              })
                              .optional()
                              .describe('Location coordinates [longitude, latitude]'),
                          })
                          .optional()
                          .describe('End place'),
                        media: zod
                          .object({
                            markdown: zod.string().describe('Markdown'),
                            assets: zod
                              .object({
                                logo: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('Logo'),
                                images: zod
                                  .array(
                                    zod.object({
                                      id: zod.string().describe('ID (TSID)'),
                                      fileName: zod.string().describe('Filename'),
                                      contentType: zod.string().describe('Content-Type'),
                                      url: zod.string().describe('url'),
                                      imageUrl: zod
                                        .string()
                                        .optional()
                                        .describe('image template url'),
                                      imageDimensions: zod
                                        .object({
                                          width: zod.number().optional(),
                                          height: zod.number().optional(),
                                        })
                                        .optional()
                                        .describe('image dimensions'),
                                    })
                                  )
                                  .describe('Images'),
                                attachments: zod
                                  .array(
                                    zod.object({
                                      id: zod.string().describe('ID (TSID)'),
                                      fileName: zod.string().describe('Filename'),
                                      contentType: zod.string().describe('Content-Type'),
                                      url: zod.string().describe('url'),
                                      imageUrl: zod
                                        .string()
                                        .optional()
                                        .describe('image template url'),
                                      imageDimensions: zod
                                        .object({
                                          width: zod.number().optional(),
                                          height: zod.number().optional(),
                                        })
                                        .optional()
                                        .describe('image dimensions'),
                                    })
                                  )
                                  .describe('Attachments'),
                                originalGpx: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('Original GPX'),
                                gpx: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('GPX'),
                                fit: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('FIT'),
                                thumbnailLight: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('Light thumbnail'),
                                thumbnailDark: zod
                                  .object({
                                    id: zod.string().describe('ID (TSID)'),
                                    fileName: zod.string().describe('Filename'),
                                    contentType: zod.string().describe('Content-Type'),
                                    url: zod.string().describe('url'),
                                    imageUrl: zod
                                      .string()
                                      .optional()
                                      .describe('image template url'),
                                    imageDimensions: zod
                                      .object({
                                        width: zod.number().optional(),
                                        height: zod.number().optional(),
                                      })
                                      .optional()
                                      .describe('image dimensions'),
                                  })
                                  .optional()
                                  .describe('Dark thumbnail'),
                              })
                              .describe('Assets'),
                          })
                          .describe('Stage media'),
                        sortOrder: zod.number().describe('Sort order'),
                        stageIndex: zod
                          .number()
                          .describe(
                            "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
                          ),
                        stageCount: zod
                          .number()
                          .describe(
                            "How many live stages the trip has — the '\/ 5' of 'Day 2 \/ 5'."
                          ),
                      })
                      .describe('Trip stage information')
                  )
                  .describe('Trip stages'),
                participants: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('User ID (TSID)'),
                        displayName: zod.string().describe('User display name'),
                        avatarUrl: zod.string().optional().describe('User avatar URL'),
                      })
                      .describe('Public user information (limited fields)')
                  )
                  .describe('Trip participants'),
                thumbnailLightUrl: zod.string().optional().describe('Thumbnail URL (light)'),
                thumbnailDarkUrl: zod.string().optional().describe('Thumbnail URL (dark)'),
                thumbnailUrl: zod
                  .string()
                  .optional()
                  .describe(
                    'The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture.'
                  ),
                deleted: zod.boolean().describe('Whether the trip is soft-deleted'),
                registered: zod
                  .boolean()
                  .describe(
                    'Whether the current user is registered for this trip. False if anonymous.'
                  ),
                commentCount: zod
                  .number()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
              })
              .describe('Trip data'),
          ])
          .and(
            zod.object({
              type: zod.enum(['RIDE', 'POST', 'TRIP']).optional(),
              visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).optional(),
              name: zod.string().optional(),
            })
          )
          .describe('Publication data')
      )
      .describe('List of publications'),
    total: zod.number().describe('Total number of publications'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated publication list response')

/**
 * Set the current user's unit system, colour scheme and language. A partial update: fields omitted (or sent null) are left unchanged. These live on the server so that a member who picks imperial units on their phone sees them on the web too, and so that reinstalling the app does not lose them.
 * @summary Update display preferences
 */
export const updateMyPreferencesBodyLanguageMin = 2
export const updateMyPreferencesBodyLanguageMax = 10

export const updateMyPreferencesBodyLanguageRegExp = new RegExp(
  '^[A-Za-z]{2,3}(-[A-Za-z0-9]{2,8})*$'
)

export const UpdateMyPreferencesBody = zod
  .object({
    unitSystem: zod
      .enum(['METRIC', 'IMPERIAL'])
      .optional()
      .describe('Preferred unit system. Omit or send null to leave it unchanged.'),
    theme: zod
      .enum(['SYSTEM', 'LIGHT', 'DARK'])
      .optional()
      .describe('Preferred colour scheme. Omit or send null to leave it unchanged.'),
    language: zod
      .string()
      .min(updateMyPreferencesBodyLanguageMin)
      .max(updateMyPreferencesBodyLanguageMax)
      .regex(updateMyPreferencesBodyLanguageRegExp)
      .optional()
      .describe(
        "Preferred language as a BCP-47 tag ('fr', 'en', 'fr-CA'). Omit or send null to leave it unchanged. Not validated against the set of translations the app ships: a client asking for a language nobody has translated yet falls back on its own, which is better than a 400 the day a translation lands."
      ),
    contactableByMembers: zod
      .boolean()
      .optional()
      .describe(
        'Whether team members may reach you through the classified-ad relay. Omit or send null to leave it unchanged. Setting it to false stops the relay from delivering to you; your ads stay visible, they simply stop being answerable.'
      ),
  })
  .describe("Partial update of the current user's display preferences")

export const UpdateMyPreferencesResponse = zod
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
            serviceType: zod
              .enum(['HAMMERHEAD', 'GARMIN', 'WAHOO'])
              .describe('Service type identifier'),
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
 * Search users by display name. Pass 'teamSlug' to keep only the members of that team — useful for a picker that must yield a member, such as a ride group's leader. The parameter only ever removes results, and requires the caller to belong to the team.
 * @summary Search users
 */
export const searchUsersQueryLimitDefault = 10

export const SearchUsersQueryParams = zod.object({
  limit: zod.number().default(searchUsersQueryLimitDefault).describe('Maximum results (max 20)'),
  q: zod.string().optional().describe('Search query'),
  teamSlug: zod.string().optional().describe('Keep only members of this team'),
})

export const SearchUsersResponseItem = zod
  .object({
    id: zod.string().describe('User ID (TSID)'),
    displayName: zod.string().describe('User display name'),
    avatarUrl: zod.string().optional().describe('User avatar URL'),
  })
  .describe('Public user information (limited fields)')
export const SearchUsersResponse = zod.array(SearchUsersResponseItem)
