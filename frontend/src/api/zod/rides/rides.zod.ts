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
      .describe('Ride media'),
    dateTime: zod.iso.datetime({}).describe('Ride date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ride status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Route slug'),
    startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
    endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
    publishAt: zod.iso
      .datetime({})
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
              .describe('Average speed in km\/h'),
            maxParticipants: zod
              .number()
              .gt(createRideBodyGroupsItemMaxParticipantsExclusiveMin)
              .optional()
              .describe('Maximum participants'),
            routeSlug: zod.string().optional().describe('Route slug for this group'),
          })
          .describe('Ride group creation request')
      )
      .describe('Ride groups to create'),
  })
  .describe('Ride request')

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
      .describe('Ride media'),
    dateTime: zod.iso.datetime({}).describe('Ride date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ride status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Route slug'),
    startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
    endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
    publishAt: zod.iso
      .datetime({})
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
              .describe('Average speed in km\/h'),
            maxParticipants: zod
              .number()
              .gt(updateRideBodyGroupsItemMaxParticipantsExclusiveMin)
              .optional()
              .describe('Maximum participants'),
            routeSlug: zod.string().optional().describe('Route slug for this group'),
          })
          .describe('Ride group creation request')
      )
      .describe('Ride groups to create'),
  })
  .describe('Ride request')

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
    dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
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
    deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
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
    dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
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
    deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
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

/**
 * Join a ride group
 * @summary Join ride group
 */
export const JoinGroupParams = zod.object({
  groupId: zod.string().describe('Group ID (TSID)'),
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Leave a ride group
 * @summary Leave ride group
 */
export const LeaveGroupParams = zod.object({
  groupId: zod.string().describe('Group ID (TSID)'),
  rideSlug: zod.string().describe('Ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

/**
 * Change ride URL slug. Requires organizer permissions.
 * @summary Change ride slug
 */
export const ChangeRideSlugParams = zod.object({
  rideSlug: zod.string().describe('Current ride URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const changeRideSlugBodySlugMax = 200

export const changeRideSlugBodySlugRegExp = new RegExp('^[a-z0-9]+(-[a-z0-9]+)\*$')

export const ChangeRideSlugBody = zod
  .object({
    slug: zod
      .string()
      .max(changeRideSlugBodySlugMax)
      .regex(changeRideSlugBodySlugRegExp)
      .describe('New slug (lowercase letters, numbers, and hyphens only)'),
  })
  .describe('Slug change request')

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
    dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
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
    deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
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
    dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
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
    deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
  })
  .describe('Ride summary data')
