import * as zod from 'zod'

/**
 * Returns the current user's previews still within the 30-day retention window, most recent first
 * @summary List the current user's analysed GPX files
 */
export const listMyPreviewsQueryPageDefault = 0
export const listMyPreviewsQuerySizeDefault = 20

export const ListMyPreviewsQueryParams = zod.object({
  page: zod.number().default(listMyPreviewsQueryPageDefault).describe('Zero-based page number'),
  size: zod.number().default(listMyPreviewsQuerySizeDefault).describe('Page size'),
})

export const ListMyPreviewsResponse = zod
  .object({
    previews: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Public identifier used in URLs'),
            name: zod.string().describe('Track name'),
            distance: zod.number().describe('Distance in meters'),
            elevationGain: zod.number().describe('Total elevation gain in meters'),
            elevationLoss: zod.number().describe('Total elevation loss in meters'),
            hilliness: zod.number().describe('Elevation gain per kilometer'),
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
          })
          .describe('Summary of an analysed GPX file, for listing')
      )
      .describe('List of GPX previews'),
    total: zod.number().describe('Total number of previews'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated GPX preview list response')

/**
 * Uploads a GPX file, runs the elevation and climb pipeline, and stores the result under an unguessable identifier for 30 days
 * @summary Analyse a GPX file
 */
export const CreatePreviewBody = zod.object({
  gpxFile: zod.instanceof(File).optional(),
})

export const CreatePreviewResponse = zod
  .object({
    id: zod.string().describe('Public identifier used in URLs'),
    name: zod.string().describe('Track name'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    hilliness: zod.number().describe('Elevation gain per kilometer'),
    owned: zod.boolean().describe('Whether the current user created this preview'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    tracks: zod
      .array(
        zod
          .object({
            line: zod.object({
              type: zod.enum(['LineString']),
              coordinates: zod
                .array(zod.array(zod.number()))
                .describe('Array of [lon, lat] coordinates'),
            }),
            climbs: zod
              .array(
                zod
                  .object({
                    startDistance: zod
                      .number()
                      .describe('Start distance from route start in meters'),
                    endDistance: zod.number().describe('End distance from route start in meters'),
                    elevationGain: zod.number().describe('Elevation gain in meters'),
                    averageGradient: zod.number().describe('Average gradient percentage'),
                    maxGradient: zod.number().describe('Maximum gradient percentage'),
                    category: zod
                      .enum(['HC', 'CAT1', 'CAT2', 'CAT3', 'CAT4'])
                      .optional()
                      .describe('Climb category (HC, 1, 2, 3, 4)'),
                  })
                  .describe('Climb segment information')
              )
              .describe('List of climbs on the route'),
          })
          .describe('GPX track with track points')
      )
      .describe('Tracks'),
    waypoints: zod
      .array(
        zod.object({
          geometry: zod
            .object({
              type: zod.enum(['Point']),
              coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
            })
            .describe('Location coordinates [longitude, latitude]'),
          name: zod.string().optional(),
        })
      )
      .describe('Waypoints'),
  })
  .describe('Analysed GPX file, not attached to any team')

/**
 * Rename the preview and, when a new GPX file or planner points are provided, replay the pipeline to replace its track. Only the creator may edit.
 * @summary Update an analysed GPX file
 */
export const UpdatePreviewParams = zod.object({
  previewId: zod.string().describe('Public preview identifier'),
})

export const updatePreviewBodyPreviewNameMin = 3
export const updatePreviewBodyPreviewNameMax = 250

export const updatePreviewBodyPreviewNameRegExp = new RegExp('\\S')

export const UpdatePreviewBody = zod.object({
  preview: zod
    .object({
      name: zod
        .string()
        .min(updatePreviewBodyPreviewNameMin)
        .max(updatePreviewBodyPreviewNameMax)
        .regex(updatePreviewBodyPreviewNameRegExp)
        .describe('Preview name'),
      points: zod
        .array(
          zod.object({
            lng: zod.number(),
            lat: zod.number(),
          })
        )
        .optional()
        .describe('Points from frontend routing'),
    })
    .optional()
    .describe('GPX preview update request'),
  gpxFile: zod.instanceof(File).optional(),
})

export const UpdatePreviewResponse = zod
  .object({
    id: zod.string().describe('Public identifier used in URLs'),
    name: zod.string().describe('Track name'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    hilliness: zod.number().describe('Elevation gain per kilometer'),
    owned: zod.boolean().describe('Whether the current user created this preview'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    tracks: zod
      .array(
        zod
          .object({
            line: zod.object({
              type: zod.enum(['LineString']),
              coordinates: zod
                .array(zod.array(zod.number()))
                .describe('Array of [lon, lat] coordinates'),
            }),
            climbs: zod
              .array(
                zod
                  .object({
                    startDistance: zod
                      .number()
                      .describe('Start distance from route start in meters'),
                    endDistance: zod.number().describe('End distance from route start in meters'),
                    elevationGain: zod.number().describe('Elevation gain in meters'),
                    averageGradient: zod.number().describe('Average gradient percentage'),
                    maxGradient: zod.number().describe('Maximum gradient percentage'),
                    category: zod
                      .enum(['HC', 'CAT1', 'CAT2', 'CAT3', 'CAT4'])
                      .optional()
                      .describe('Climb category (HC, 1, 2, 3, 4)'),
                  })
                  .describe('Climb segment information')
              )
              .describe('List of climbs on the route'),
          })
          .describe('GPX track with track points')
      )
      .describe('Tracks'),
    waypoints: zod
      .array(
        zod.object({
          geometry: zod
            .object({
              type: zod.enum(['Point']),
              coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
            })
            .describe('Location coordinates [longitude, latitude]'),
          name: zod.string().optional(),
        })
      )
      .describe('Waypoints'),
  })
  .describe('Analysed GPX file, not attached to any team')

/**
 * Anyone holding the link may read the preview: the unguessable identifier is what grants access. Creating and deleting require an account.
 * @summary Get an analysed GPX file
 */
export const GetPreviewParams = zod.object({
  previewId: zod.string().describe('Public preview identifier'),
})

export const GetPreviewResponse = zod
  .object({
    id: zod.string().describe('Public identifier used in URLs'),
    name: zod.string().describe('Track name'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    hilliness: zod.number().describe('Elevation gain per kilometer'),
    owned: zod.boolean().describe('Whether the current user created this preview'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    tracks: zod
      .array(
        zod
          .object({
            line: zod.object({
              type: zod.enum(['LineString']),
              coordinates: zod
                .array(zod.array(zod.number()))
                .describe('Array of [lon, lat] coordinates'),
            }),
            climbs: zod
              .array(
                zod
                  .object({
                    startDistance: zod
                      .number()
                      .describe('Start distance from route start in meters'),
                    endDistance: zod.number().describe('End distance from route start in meters'),
                    elevationGain: zod.number().describe('Elevation gain in meters'),
                    averageGradient: zod.number().describe('Average gradient percentage'),
                    maxGradient: zod.number().describe('Maximum gradient percentage'),
                    category: zod
                      .enum(['HC', 'CAT1', 'CAT2', 'CAT3', 'CAT4'])
                      .optional()
                      .describe('Climb category (HC, 1, 2, 3, 4)'),
                  })
                  .describe('Climb segment information')
              )
              .describe('List of climbs on the route'),
          })
          .describe('GPX track with track points')
      )
      .describe('Tracks'),
    waypoints: zod
      .array(
        zod.object({
          geometry: zod
            .object({
              type: zod.enum(['Point']),
              coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
            })
            .describe('Location coordinates [longitude, latitude]'),
          name: zod.string().optional(),
        })
      )
      .describe('Waypoints'),
  })
  .describe('Analysed GPX file, not attached to any team')

/**
 * Only the creator may delete
 * @summary Delete an analysed GPX file
 */
export const DeletePreviewParams = zod.object({
  previewId: zod.string().describe('Public preview identifier'),
})

export const DeletePreviewResponse = zod.void()

/**
 * Uploads the preview to the current user's connected Garmin or Hammerhead
 * @summary Send an analysed GPX file to a GPS service
 */
export const UploadToGpsServiceParams = zod.object({
  previewId: zod.string().describe('Public preview identifier'),
  serviceType: zod.enum(['HAMMERHEAD', 'GARMIN']).describe('GPS service type'),
})

export const UploadToGpsServiceResponse = zod
  .object({
    success: zod.boolean().describe('Whether the upload was successful'),
    message: zod.string().optional().describe('Error message if upload failed'),
    externalRouteId: zod.string().optional().describe('External route ID on the GPS service'),
  })
  .describe('Result of uploading a route to a GPS service')

/**
 * Creates a team route from the preview's original upload
 * @summary Save an analysed GPX file as a route
 */
export const CreateRouteFromPreviewParams = zod.object({
  previewId: zod.string().describe('Public preview identifier'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createRouteFromPreviewBodyNameMin = 3
export const createRouteFromPreviewBodyNameMax = 200

export const createRouteFromPreviewBodyNameRegExp = new RegExp('\\S')

export const CreateRouteFromPreviewBody = zod
  .object({
    name: zod
      .string()
      .min(createRouteFromPreviewBodyNameMin)
      .max(createRouteFromPreviewBodyNameMax)
      .regex(createRouteFromPreviewBodyNameRegExp)
      .describe('Route name'),
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
      .describe('Media'),
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is publicly visible'),
    points: zod
      .array(
        zod.object({
          lng: zod.number(),
          lat: zod.number(),
        })
      )
      .optional()
      .describe('Points from frontend routing'),
  })
  .describe('Route update request')

export const CreateRouteFromPreviewResponse = zod
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
      .describe('Route description'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
  })
  .describe('Route summary data')
