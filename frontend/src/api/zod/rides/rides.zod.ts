import * as zod from 'zod'

/**
 * Create a new ride with optional groups
 * @summary Create ride
 */
export const CreateRideParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createRideBodyNameMin = 3
export const createRideBodyNameMax = 200

export const createRideBodyNameRegExp = new RegExp('\\S')
export const createRideBodyMediaMarkdownMax = 100000

export const createRideBodyGroupsItemNameMax = 200

export const createRideBodyGroupsItemNameRegExp = new RegExp('\\S')
export const createRideBodyGroupsItemAverageSpeedExclusiveMin = 0

export const createRideBodyGroupsItemMaxParticipantsExclusiveMin = 0

export const CreateRideBody = zod
  .object({
    name: zod
      .string()
      .min(createRideBodyNameMin)
      .max(createRideBodyNameMax)
      .regex(createRideBodyNameRegExp)
      .describe('Ride name'),
    media: zod
      .object({
        markdown: zod.string().max(createRideBodyMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
              })
              .optional()
              .describe('Dark thumbnail'),
          })
          .describe('Assets'),
      })
      .describe('Ride media'),
    dateTime: zod.iso.datetime({ offset: true }).describe('Ride date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ride status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Route slug'),
    startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
    endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
    publishAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Publication timestamp (for scheduled publishing)'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().optional().describe('id'),
            name: zod
              .string()
              .min(1)
              .max(createRideBodyGroupsItemNameMax)
              .regex(createRideBodyGroupsItemNameRegExp)
              .describe('Group name'),
            time: zod.string().optional(),
            averageSpeed: zod
              .number()
              .gt(createRideBodyGroupsItemAverageSpeedExclusiveMin)
              .optional()
              .describe('Average speed in km/h'),
            maxParticipants: zod
              .int()
              .gt(createRideBodyGroupsItemMaxParticipantsExclusiveMin)
              .optional()
              .describe('Maximum participants'),
            routeSlug: zod.string().optional().describe('Route slug for this group'),
            leaderId: zod
              .string()
              .optional()
              .describe(
                "ID (TSID) of the member who leads this group. Must belong to the team owning the ride. Omit or send null for no designated leader — clients then show no leader at all rather than falling back on the ride's creator."
              ),
          })
          .describe('Ride group creation request')
      )
      .describe('Ride groups to create'),
  })
  .describe('Ride request')

export const createRideResponseMediaMarkdownMax = 100000

export const CreateRideResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(createRideResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')

/**
 * Update ride information. Requires organizer permissions.
 * @summary Update ride
 */
export const UpdateRideParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const updateRideBodyNameMin = 3
export const updateRideBodyNameMax = 200

export const updateRideBodyNameRegExp = new RegExp('\\S')
export const updateRideBodyMediaMarkdownMax = 100000

export const updateRideBodyGroupsItemNameMax = 200

export const updateRideBodyGroupsItemNameRegExp = new RegExp('\\S')
export const updateRideBodyGroupsItemAverageSpeedExclusiveMin = 0

export const updateRideBodyGroupsItemMaxParticipantsExclusiveMin = 0

export const UpdateRideBody = zod
  .object({
    name: zod
      .string()
      .min(updateRideBodyNameMin)
      .max(updateRideBodyNameMax)
      .regex(updateRideBodyNameRegExp)
      .describe('Ride name'),
    media: zod
      .object({
        markdown: zod.string().max(updateRideBodyMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
              })
              .optional()
              .describe('Dark thumbnail'),
          })
          .describe('Assets'),
      })
      .describe('Ride media'),
    dateTime: zod.iso.datetime({ offset: true }).describe('Ride date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ride status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Route slug'),
    startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
    endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
    publishAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Publication timestamp (for scheduled publishing)'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().optional().describe('id'),
            name: zod
              .string()
              .min(1)
              .max(updateRideBodyGroupsItemNameMax)
              .regex(updateRideBodyGroupsItemNameRegExp)
              .describe('Group name'),
            time: zod.string().optional(),
            averageSpeed: zod
              .number()
              .gt(updateRideBodyGroupsItemAverageSpeedExclusiveMin)
              .optional()
              .describe('Average speed in km/h'),
            maxParticipants: zod
              .int()
              .gt(updateRideBodyGroupsItemMaxParticipantsExclusiveMin)
              .optional()
              .describe('Maximum participants'),
            routeSlug: zod.string().optional().describe('Route slug for this group'),
            leaderId: zod
              .string()
              .optional()
              .describe(
                "ID (TSID) of the member who leads this group. Must belong to the team owning the ride. Omit or send null for no designated leader — clients then show no leader at all rather than falling back on the ride's creator."
              ),
          })
          .describe('Ride group creation request')
      )
      .describe('Ride groups to create'),
  })
  .describe('Ride request')

export const updateRideResponseMediaMarkdownMax = 100000

export const UpdateRideResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(updateRideResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')

/**
 * Get detailed ride information including groups
 * @summary Get ride details
 */
export const GetRideParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const getRideResponseMediaMarkdownMax = 100000

export const GetRideResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(getRideResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')

/**
 * Soft delete a ride. Requires organizer permissions.
 * @summary Delete ride
 */
export const DeleteRideParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteRideResponse = zod.void()

/**
 * Join a ride group
 * @summary Join ride group
 */
export const JoinGroupParams = zod.object({
  groupId: zod.string().describe('Group ID (TSID)'),
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const JoinGroupResponse = zod
  .object({
    id: zod.string().describe('Participation ID (TSID)'),
    userId: zod.string().describe('User ID (TSID)'),
    registeredAt: zod.iso.datetime({ offset: true }).optional().describe('Registration timestamp'),
  })
  .describe('Ride participation information')

/**
 * Leave a ride group
 * @summary Leave ride group
 */
export const LeaveGroupParams = zod.object({
  groupId: zod.string().describe('Group ID (TSID)'),
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const LeaveGroupResponse = zod.void()

/**
 * One VEVENT for the ride, to add it on its own to a calendar. Readable by whoever may read the ride; no calendar token.
 * @summary Download ride as a calendar file
 */
export const DownloadRideIcsParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DownloadRideIcsResponse = zod.unknown()

/**
 * One page of the people registered to the ride, or to one of its groups, earliest registrations first, searchable by display name. The ride detail only embeds the first few; this is the whole list, with its total. Readable by whoever may read the ride.
 * @summary List ride participants
 */
export const GetRideParticipantsParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const getRideParticipantsQueryPageDefault = 0
export const getRideParticipantsQuerySizeDefault = 50

export const GetRideParticipantsQueryParams = zod.object({
  groupId: zod
    .string()
    .optional()
    .describe('Only this group of the ride (TSID); every group when absent'),
  page: zod.int().default(getRideParticipantsQueryPageDefault).describe('Page number (0-based)'),
  search: zod.string().optional().describe('Search by display name'),
  size: zod.int().default(getRideParticipantsQuerySizeDefault).describe('Page size'),
})

export const GetRideParticipantsResponse = zod
  .object({
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
      .describe('Participants of this page, in registration order (earliest first)'),
    total: zod
      .int()
      .describe(
        'Number of participants matching the search, over every page — the M of « N of M »'
      ),
    page: zod.int().describe('Current page number (0-based)'),
    size: zod.int().describe('Page size actually applied'),
  })
  .describe('Paginated list of the people registered to a ride, a ride group or a trip')

/**
 * Change ride URL slug. Requires organizer permissions.
 * @summary Change ride slug
 */
export const ChangeRideSlugParams = zod.object({
  rideSlug: zod.string().describe('Current ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const changeRideSlugBodySlugMax = 200

export const changeRideSlugBodySlugRegExp = new RegExp('^[a-z0-9]+(-[a-z0-9]+)*$')

export const ChangeRideSlugBody = zod
  .object({
    slug: zod
      .string()
      .max(changeRideSlugBodySlugMax)
      .regex(changeRideSlugBodySlugRegExp)
      .describe('New slug (lowercase letters, numbers, and hyphens only)'),
  })
  .describe('Slug change request')

export const changeRideSlugResponseMediaMarkdownMax = 100000

export const ChangeRideSlugResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(changeRideSlugResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')

/**
 * Change the ride's status and nothing else — what a list row can do without the full ride. Same side effects as a status change through the update. Requires organizer permissions.
 * @summary Change ride status
 */
export const ChangeRideStatusParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const ChangeRideStatusBody = zod
  .object({
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('New status'),
  })
  .describe('Status change request')

export const changeRideStatusResponseMediaMarkdownMax = 100000

export const ChangeRideStatusResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(changeRideStatusResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')

/**
 * Restore a soft-deleted ride. Requires organizer permissions.
 * @summary Restore ride
 */
export const UndeleteRideParams = zod.object({
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const undeleteRideResponseMediaMarkdownMax = 100000

export const UndeleteRideResponse = zod
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
        logoUrl: zod
          .string()
          .optional()
          .describe(
            "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
          ),
      })
      .describe('Team'),
    id: zod.string().describe('Publication ID (TSID)'),
    slug: zod.string().describe('Publication URL slug'),
    name: zod.string().describe('Publication name'),
    media: zod
      .object({
        markdown: zod.string().max(undeleteRideResponseMediaMarkdownMax).describe('Markdown'),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                      width: zod.int().optional(),
                      height: zod.int().optional(),
                    })
                    .optional()
                    .describe('image dimensions'),
                  size: zod
                    .int()
                    .optional()
                    .describe(
                      'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                    ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
                    width: zod.int().optional(),
                    height: zod.int().optional(),
                  })
                  .optional()
                  .describe('image dimensions'),
                size: zod
                  .int()
                  .optional()
                  .describe(
                    'Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests.'
                  ),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Publication date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the ride is over, computed by the server when the response is built: its start time has passed. Independent of status — a past cancelled ride is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    groupCount: zod.int().describe('Number of groups'),
    groups: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Group ID (TSID)'),
            name: zod.string().describe('Group name'),
            time: zod.string().optional(),
            routeSlug: zod.string().optional().describe('Route slug'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
            maxParticipants: zod.int().optional().describe('Maximum participants'),
            countParticipants: zod.int().describe('Current number of participants'),
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
              .describe(
                'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
              ),
            sortOrder: zod.int().describe('Sort order'),
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
              .describe('Total elevation gain in meters of the group route, if it has one'),
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
            thumbnailLightUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (light) of the group route, if it has one'),
            thumbnailDarkUrl: zod
              .string()
              .optional()
              .describe('Thumbnail URL (dark) of the group route, if it has one'),
            thumbnailUrl: zod
              .string()
              .optional()
              .describe(
                "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
            coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
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
      .describe('ID (TSID) of the group the current user joined, null if not registered'),
    registeredGroup: zod
      .object({
        id: zod.string().describe('Group ID (TSID)'),
        name: zod.string().describe('Group name'),
        time: zod.string().optional(),
        routeSlug: zod.string().optional().describe('Route slug'),
        averageSpeed: zod.number().optional().describe('Average speed in km/h'),
        maxParticipants: zod.int().optional().describe('Maximum participants'),
        countParticipants: zod.int().describe('Current number of participants'),
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
          .describe(
            'The first participants of the group (at most 8), earliest registrations first — enough to draw avatars. countParticipants is the total; the whole list is paginated and searched by GET …/rides/{rideSlug}/participants?groupId=.'
          ),
        sortOrder: zod.int().describe('Sort order'),
        registered: zod
          .boolean()
          .describe('Whether the current user is registered in THIS group. False if anonymous.'),
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
          .describe('Total elevation gain in meters of the group route, if it has one'),
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
        thumbnailLightUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (light) of the group route, if it has one'),
        thumbnailDarkUrl: zod
          .string()
          .optional()
          .describe('Thumbnail URL (dark) of the group route, if it has one'),
        thumbnailUrl: zod
          .string()
          .optional()
          .describe(
            "The one thumbnail of the group route to show when the client does not theme its cards: the light variant if there is one, else the dark one. Null when the group has no route or its route has no thumbnail — the ride's own thumbnail is then the one to fall back on."
          ),
      })
      .optional()
      .describe(
        'The group the current user joined, in full — the same object as the matching entry of groups. Null if not registered or anonymous. Set on list rows too, where groups is empty: a client rendering "my next ride" needs no second request for its group. Its leader is the group\'s own, null when none was designated.'
      ),
    full: zod
      .boolean()
      .describe(
        'Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants.'
      ),
    maxParticipants: zod
      .int()
      .optional()
      .describe(
        'Capacity of the whole ride: the sum of its groups\' maxParticipants, to render participantCount against it ("12/40"). Null when the ride has no group, or when at least one group has no maxParticipants — the ride then has no overall limit, and is never full. Set on list rows too, where groups is empty.'
      ),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Ride summary data')
