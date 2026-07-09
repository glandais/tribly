import * as zod from 'zod'

/**
 * Get paginated list of routes from all accessible teams (user's teams + public teams)
 * @summary List all routes
 */
export const listAllRoutesQueryPageDefault = 0
export const listAllRoutesQuerySizeDefault = 20

export const ListAllRoutesQueryParams = zod.object({
  hilliness: zod
    .enum(['FLAT', 'HILLY', 'MOUNTAINOUS'])
    .optional()
    .describe('Hilliness preset (FLAT, HILLY, MOUNTAINOUS)'),
  maxDistance: zod.number().optional().describe('Maximum distance in meters'),
  maxElevationGain: zod.number().optional().describe('Maximum elevation gain in meters'),
  minDistance: zod.number().optional().describe('Minimum distance in meters'),
  minElevationGain: zod.number().optional().describe('Minimum elevation gain in meters'),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod.number().optional().describe('Search radius in meters (default: 25000)'),
  nearType: zod
    .enum(['START', 'END', 'START_OR_END'])
    .optional()
    .describe('Search near START, END, or START_OR_END (default)'),
  page: zod.number().default(listAllRoutesQueryPageDefault).describe('Page number (0-indexed)'),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.number().default(listAllRoutesQuerySizeDefault).describe('Page size'),
  sortBy: zod
    .enum(['DISTANCE', 'ELEVATION_GAIN', 'HILLINESS', 'DATE_TIME'])
    .optional()
    .describe('Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)'),
  sortDir: zod.enum(['ASC', 'DESC']).optional().describe('Sort direction (ASC, DESC)'),
  surfaceType: zod
    .enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED'])
    .optional()
    .describe('Filter by surface type'),
  windDirection: zod
    .enum([
      'NORTH',
      'NORTH_EAST',
      'EAST',
      'SOUTH_EAST',
      'SOUTH',
      'SOUTH_WEST',
      'WEST',
      'NORTH_WEST',
    ])
    .optional()
    .describe('Filter by wind direction'),
})

export const ListAllRoutesResponse = zod
  .object({
    routes: zod
      .array(
        zod
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
      )
      .describe('List of routes'),
    total: zod.number().describe('Total number of routes'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated route list response')

/**
 * Get paginated list of routes for a team with optional filters and sorting
 * @summary List routes
 */
export const ListRoutesParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const listRoutesQueryPageDefault = 0
export const listRoutesQuerySizeDefault = 20

export const ListRoutesQueryParams = zod.object({
  hilliness: zod
    .enum(['FLAT', 'HILLY', 'MOUNTAINOUS'])
    .optional()
    .describe('Hilliness preset (FLAT, HILLY, MOUNTAINOUS)'),
  maxDistance: zod.number().optional().describe('Maximum distance in meters'),
  maxElevationGain: zod.number().optional().describe('Maximum elevation gain in meters'),
  minDistance: zod.number().optional().describe('Minimum distance in meters'),
  minElevationGain: zod.number().optional().describe('Minimum elevation gain in meters'),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod.number().optional().describe('Search radius in meters (default: 25000)'),
  nearType: zod
    .enum(['START', 'END', 'START_OR_END'])
    .optional()
    .describe('Search near START, END, or START_OR_END (default)'),
  page: zod.number().default(listRoutesQueryPageDefault).describe('Page number (0-indexed)'),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.number().default(listRoutesQuerySizeDefault).describe('Page size'),
  sortBy: zod
    .enum(['DISTANCE', 'ELEVATION_GAIN', 'HILLINESS', 'DATE_TIME'])
    .optional()
    .describe('Sort by field (DISTANCE, ELEVATION_GAIN, HILLINESS, DATE_TIME)'),
  sortDir: zod.enum(['ASC', 'DESC']).optional().describe('Sort direction (ASC, DESC)'),
  surfaceType: zod
    .enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED'])
    .optional()
    .describe('Filter by surface type'),
  windDirection: zod
    .enum([
      'NORTH',
      'NORTH_EAST',
      'EAST',
      'SOUTH_EAST',
      'SOUTH',
      'SOUTH_WEST',
      'WEST',
      'NORTH_WEST',
    ])
    .optional()
    .describe('Filter by wind direction'),
})

export const ListRoutesResponse = zod
  .object({
    routes: zod
      .array(
        zod
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
      )
      .describe('List of routes'),
    total: zod.number().describe('Total number of routes'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated route list response')

/**
 * Create a new route by uploading a GPX file
 * @summary Create route
 */
export const CreateRouteParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createRouteBodyRouteNameMin = 3
export const createRouteBodyRouteNameMax = 200

export const createRouteBodyRouteNameRegExp = new RegExp('\\S')

export const CreateRouteBody = zod.object({
  route: zod
    .object({
      name: zod
        .string()
        .min(createRouteBodyRouteNameMin)
        .max(createRouteBodyRouteNameMax)
        .regex(createRouteBodyRouteNameRegExp)
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
    .optional()
    .describe('Route update request'),
  gpxFile: zod.instanceof(File).optional(),
})

export const CreateRouteResponse = zod
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

/**
 * Update route metadata (name, markdown, etc.) and optionally replace the GPX file. If a new GPX file is provided, the old track data and climbs will be replaced.
 * @summary Update route
 */
export const UpdateRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const updateRouteBodyRouteNameMin = 3
export const updateRouteBodyRouteNameMax = 200

export const updateRouteBodyRouteNameRegExp = new RegExp('\\S')

export const UpdateRouteBody = zod.object({
  route: zod
    .object({
      name: zod
        .string()
        .min(updateRouteBodyRouteNameMin)
        .max(updateRouteBodyRouteNameMax)
        .regex(updateRouteBodyRouteNameRegExp)
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
    .optional()
    .describe('Route update request'),
  gpxFile: zod.instanceof(File).optional(),
})

export const UpdateRouteResponse = zod
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

/**
 * Get detailed route information including GPS coordinates and statistics
 * @summary Get route details
 */
export const GetRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetRouteResponse = zod
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
      .describe('Media'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    start: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    end: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    createdBy: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
      })
      .describe('Creator user'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Last update timestamp'),
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
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
  })
  .describe('Detailed route information')

/**
 * Soft delete a route. Requires route creator or team admin permissions.
 * @summary Delete route
 */
export const DeleteRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteRouteResponse = zod.void()

/**
 * Change route URL slug. Requires organizer permissions.
 * @summary Change route slug
 */
export const ChangeRouteSlugParams = zod.object({
  routeSlug: zod.string().describe('Current route URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const changeRouteSlugBodySlugMax = 200

export const changeRouteSlugBodySlugRegExp = new RegExp('^[a-z0-9]+(-[a-z0-9]+)*$')

export const ChangeRouteSlugBody = zod
  .object({
    slug: zod
      .string()
      .max(changeRouteSlugBodySlugMax)
      .regex(changeRouteSlugBodySlugRegExp)
      .describe('New slug (lowercase letters, numbers, and hyphens only)'),
  })
  .describe('Slug change request')

export const ChangeRouteSlugResponse = zod
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
      .describe('Media'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    start: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    end: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    createdBy: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
      })
      .describe('Creator user'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Last update timestamp'),
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
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
  })
  .describe('Detailed route information')

/**
 * Restore a soft-deleted route. Requires route creator or team admin permissions.
 * @summary Restore route
 */
export const UndeleteRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const UndeleteRouteResponse = zod
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
      .describe('Media'),
    distance: zod.number().describe('Distance in meters'),
    elevationGain: zod.number().describe('Total elevation gain in meters'),
    elevationLoss: zod.number().describe('Total elevation loss in meters'),
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    start: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    end: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    createdBy: zod
      .object({
        id: zod.string().describe('User ID (TSID)'),
        displayName: zod.string().describe('User display name'),
        avatarUrl: zod.string().optional().describe('User avatar URL'),
      })
      .describe('Creator user'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Last update timestamp'),
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
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
  })
  .describe('Detailed route information')
