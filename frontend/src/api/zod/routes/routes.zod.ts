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
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only routes from teams where the user has at least this role. Yields nothing for an anonymous visitor.'
    ),
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
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
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
            surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
            visibility: zod
              .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
              .describe('Whether the route is public'),
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            deleted: zod.boolean().describe('Whether the route is soft-deleted'),
            commentCount: zod
              .number()
              .optional()
              .describe(
                'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
              ),
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
 * Extent enclosing the routes of all accessible teams, so a map can open framed on them. Accepts the same filters as the route list, minus sorting and pagination. Yields a null box when no route matches.
 * @summary All routes bounding box
 */
export const GetAllRoutesBoundsQueryParams = zod.object({
  hilliness: zod
    .enum(['FLAT', 'HILLY', 'MOUNTAINOUS'])
    .optional()
    .describe('Hilliness preset (FLAT, HILLY, MOUNTAINOUS)'),
  maxDistance: zod.number().optional().describe('Maximum distance in meters'),
  maxElevationGain: zod.number().optional().describe('Maximum elevation gain in meters'),
  minDistance: zod.number().optional().describe('Minimum distance in meters'),
  minElevationGain: zod.number().optional().describe('Minimum elevation gain in meters'),
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only routes from teams where the user has at least this role. Yields a null box for an anonymous visitor.'
    ),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod.number().optional().describe('Search radius in meters (default: 25000)'),
  nearType: zod
    .enum(['START', 'END', 'START_OR_END'])
    .optional()
    .describe('Search near START, END, or START_OR_END (default)'),
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const GetAllRoutesBoundsResponse = zod
  .object({
    bounds: zod
      .object({
        minLon: zod.number().describe('Western edge'),
        minLat: zod.number().describe('Southern edge'),
        maxLon: zod.number().describe('Eastern edge'),
        maxLat: zod.number().describe('Northern edge'),
      })
      .optional()
      .describe('Bounding box, or null when no route matches'),
  })
  .describe('Bounding box of the routes matching a filter set')

/**
 * How many routes of all accessible teams match the filters, with none of them read. Accepts exactly the same filters as the route list, minus sorting and pagination, so the figure and the list it opens can never disagree. Meant for a filter sheet that wants to announce its result count before the user commits to it.
 * @summary Count all routes
 */
export const CountAllRoutesQueryParams = zod.object({
  hilliness: zod
    .enum(['FLAT', 'HILLY', 'MOUNTAINOUS'])
    .optional()
    .describe('Hilliness preset (FLAT, HILLY, MOUNTAINOUS)'),
  maxDistance: zod.number().optional().describe('Maximum distance in meters'),
  maxElevationGain: zod.number().optional().describe('Maximum elevation gain in meters'),
  minDistance: zod.number().optional().describe('Minimum distance in meters'),
  minElevationGain: zod.number().optional().describe('Minimum elevation gain in meters'),
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only routes from teams where the user has at least this role. Yields zero for an anonymous visitor.'
    ),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod.number().optional().describe('Search radius in meters (default: 25000)'),
  nearType: zod
    .enum(['START', 'END', 'START_OR_END'])
    .optional()
    .describe('Search near START, END, or START_OR_END (default)'),
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const CountAllRoutesResponse = zod
  .object({
    total: zod.number().describe('Total number of matching items'),
  })
  .describe('Number of items matching a filter set')

/**
 * Mapbox vector tile holding the routes of all accessible teams, layer 'routes'. Accepts the same filters as the route list, minus sorting and pagination, which a tile has no use for. Fetched directly by the map renderer, so it authenticates with the session cookie rather than a bearer token.
 * @summary All routes vector tile
 */
export const AllRoutesTileParams = zod.object({
  x: zod.number().describe('Tile column'),
  y: zod.number().describe('Tile row'),
  z: zod.number().describe('Zoom level'),
})

export const AllRoutesTileQueryParams = zod.object({
  hilliness: zod
    .enum(['FLAT', 'HILLY', 'MOUNTAINOUS'])
    .optional()
    .describe('Hilliness preset (FLAT, HILLY, MOUNTAINOUS)'),
  maxDistance: zod.number().optional().describe('Maximum distance in meters'),
  maxElevationGain: zod.number().optional().describe('Maximum elevation gain in meters'),
  minDistance: zod.number().optional().describe('Minimum distance in meters'),
  minElevationGain: zod.number().optional().describe('Minimum elevation gain in meters'),
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only routes from teams where the user has at least this role. Yields an empty tile for an anonymous visitor.'
    ),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod.number().optional().describe('Search radius in meters (default: 25000)'),
  nearType: zod
    .enum(['START', 'END', 'START_OR_END'])
    .optional()
    .describe('Search near START, END, or START_OR_END (default)'),
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const AllRoutesTileResponse = zod.unknown()

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
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
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
            surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
            visibility: zod
              .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
              .describe('Whether the route is public'),
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            deleted: zod.boolean().describe('Whether the route is soft-deleted'),
            commentCount: zod
              .number()
              .optional()
              .describe(
                'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
              ),
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
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
    commentCount: zod
      .number()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Route summary data')

/**
 * Extent enclosing the team's routes, so a map can open framed on them. Accepts the same filters as the route list, minus sorting and pagination. Yields a null box when no route matches.
 * @summary Team routes bounding box
 */
export const GetRoutesBoundsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetRoutesBoundsQueryParams = zod.object({
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
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const GetRoutesBoundsResponse = zod
  .object({
    bounds: zod
      .object({
        minLon: zod.number().describe('Western edge'),
        minLat: zod.number().describe('Southern edge'),
        maxLon: zod.number().describe('Eastern edge'),
        maxLat: zod.number().describe('Northern edge'),
      })
      .optional()
      .describe('Bounding box, or null when no route matches'),
  })
  .describe('Bounding box of the routes matching a filter set')

/**
 * The detail of every requested 'slug' that exists and the caller may read, in one round-trip — built for the screens that load several routes together (a ride's stages, a comparison view), which would otherwise cost one request per route. Accepts the same 'simplify' and 'points' geometry knobs as the single-route endpoint, plus an optional elevation profile per route. Unknown slugs and slugs the caller may not read are silently left out of the answer rather than failing the whole batch. When the batch resolves to a single route, 'simplify'/'points' behave exactly as on the single-route endpoint — including returning the stored track unchanged when neither is given. Past one route, the per-route point count is capped at 1000 regardless of what 'simplify'/'points' resolve to, so a request naming many slugs cannot be used to pull the full stored geometry of all of them at once. The response also carries the bounding box of the track geometry actually sent back (waypoints are excluded, so an imported meeting-point or car-park waypoint far off the track cannot widen it), so a map can frame the batch without a second request.
 * @summary Get several routes' details at once
 */
export const GetRoutesBulkParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const getRoutesBulkQueryElevationDefault = false
export const getRoutesBulkQueryElevationSamplesDefault = 300

export const GetRoutesBulkQueryParams = zod.object({
  elevation: zod
    .boolean()
    .default(getRoutesBulkQueryElevationDefault)
    .describe("Whether to attach each route's sampled elevation profile."),
  elevationSamples: zod
    .number()
    .default(getRoutesBulkQueryElevationSamplesDefault)
    .describe(
      "Resolution of the elevation profile when 'elevation' is true. Same clamping as the single-route elevation-profile endpoint."
    ),
  points: zod
    .number()
    .optional()
    .describe(
      "Maximum number of track points per route, applied to every route of the batch — same semantics as on the single-route endpoint. Once the batch resolves to more than one route, this is capped at 1000 per route regardless of the value passed here (or of 'simplify'), so a request naming many slugs cannot be used to pull the full stored geometry of all of them at once."
    ),
  simplify: zod
    .number()
    .optional()
    .describe(
      'Douglas-Peucker tolerance in meters, applied to every route of the batch — same semantics as on the single-route endpoint.'
    ),
  slug: zod
    .array(zod.string())
    .optional()
    .describe(
      "Route slug to include, repeatable. Capped at 50; unknown slugs and slugs the caller may not read are silently omitted from the response rather than erroring. Unlike GET \/{routeSlug}, a slug that was renamed is also omitted rather than followed to the route's current slug: the single-route endpoint falls back to the rename history, this one does not. Same 'omit, never fail' contract as an unknown or unreadable slug, just for a different reason."
    ),
})

export const GetRoutesBulkResponse = zod
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
                            endDistance: zod
                              .number()
                              .describe('End distance from route start in meters'),
                            elevationGain: zod.number().describe('Elevation gain in meters'),
                            averageGradient: zod.number().describe('Average gradient percentage'),
                            maxGradient: zod.number().describe('Maximum gradient percentage'),
                            category: zod
                              .enum(['HC', 'CAT1', 'CAT2', 'CAT3', 'CAT4'])
                              .optional()
                              .describe('Climb category (HC, 1, 2, 3, 4)'),
                            parts: zod
                              .array(
                                zod
                                  .object({
                                    startDistance: zod
                                      .number()
                                      .describe('Start distance from route start in meters'),
                                    endDistance: zod
                                      .number()
                                      .describe('End distance from route start in meters'),
                                    elevationGain: zod
                                      .number()
                                      .describe('Elevation gain in meters'),
                                    grade: zod.number().describe('Gradient percentage'),
                                  })
                                  .describe('Climb part information')
                              )
                              .describe('Gradient segments making up the climb'),
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
                      coordinates: zod
                        .array(zod.number())
                        .describe('Coordinates [longitude, latitude]'),
                    })
                    .describe('Location coordinates [longitude, latitude]'),
                  name: zod.string().optional(),
                })
              )
              .describe('Waypoints'),
            deleted: zod.boolean().describe('Whether the route is soft-deleted'),
            commentCount: zod
              .number()
              .optional()
              .describe(
                'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
              ),
            elevationProfile: zod
              .object({
                routeId: zod.string().describe('Route ID (TSID)'),
                slug: zod.string().describe('Route slug'),
                distance: zod.number().describe('Distance covered by the profile, in meters'),
                minElevation: zod.number().describe('Lowest elevation of the profile, in meters'),
                maxElevation: zod.number().describe('Highest elevation of the profile, in meters'),
                samples: zod
                  .number()
                  .describe(
                    'Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.'
                  ),
                points: zod
                  .array(
                    zod
                      .object({
                        distance: zod
                          .number()
                          .describe('Cumulative distance from the start of the route, in meters'),
                        elevation: zod.number().describe('Elevation above sea level, in meters'),
                        grade: zod
                          .number()
                          .describe(
                            'Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.'
                          ),
                      })
                      .describe('One point of an elevation profile')
                  )
                  .describe('Profile points, by increasing distance'),
              })
              .optional()
              .describe(
                "Sampled elevation profile of the route. Absent unless explicitly requested (the bulk route endpoint's 'elevation' flag) — computing and serialising it costs nothing to skip, so every other caller of this DTO gets exactly what it got before this field existed."
              ),
          })
          .describe('Detailed route information')
      )
      .describe('Route details, one per readable requested slug'),
    extent: zod
      .object({
        minLon: zod.number().describe('Western edge'),
        minLat: zod.number().describe('Southern edge'),
        maxLon: zod.number().describe('Eastern edge'),
        maxLat: zod.number().describe('Northern edge'),
      })
      .optional()
      .describe('Bounding box of every returned route, or null when routes is empty'),
  })
  .describe('Detail of several routes fetched at once, plus their combined extent')

/**
 * How many of the team's routes match the filters, with none of them read. Accepts exactly the same filters as the route list, minus sorting and pagination, so the figure and the list it opens can never disagree. Meant for a filter sheet that wants to announce its result count before the user commits to it.
 * @summary Count routes
 */
export const CountRoutesParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const CountRoutesQueryParams = zod.object({
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
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const CountRoutesResponse = zod
  .object({
    total: zod.number().describe('Total number of matching items'),
  })
  .describe('Number of items matching a filter set')

/**
 * Mapbox vector tile holding the team's routes, layer 'routes'. Accepts the same filters as the route list, minus sorting and pagination, which a tile has no use for. Fetched directly by the map renderer, so it authenticates with the session cookie rather than a bearer token.
 * @summary Team routes vector tile
 */
export const RoutesTileParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  x: zod.number().describe('Tile column'),
  y: zod.number().describe('Tile row'),
  z: zod.number().describe('Zoom level'),
})

export const RoutesTileQueryParams = zod.object({
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
  search: zod.string().optional().describe('Search by name\/markdown'),
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

export const RoutesTileResponse = zod.unknown()

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
    surfaceType: zod.enum(['ROAD', 'GRAVEL', 'MTB', 'MIXED']).describe('Surface type'),
    visibility: zod
      .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
      .describe('Whether the route is public'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    deleted: zod.boolean().describe('Whether the route is soft-deleted'),
    commentCount: zod
      .number()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
  })
  .describe('Route summary data')

/**
 * Get detailed route information including GPS coordinates and statistics. The stored track holds one point every ten meters, which is megabytes of JSON on a long route: 'simplify' and 'points' let a client trade fidelity for weight. Passing neither returns the stored track unchanged.
 * @summary Get route details
 */
export const GetRouteParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetRouteQueryParams = zod.object({
  points: zod
    .number()
    .optional()
    .describe(
      "Maximum number of track points to return. The points kept are those deviating most from the simplified line — corners and elevation extrema survive, straight flat stretches are dropped — and the first and last points are always kept. Applied after 'simplify' when both are given. Absent, zero or a value larger than the stored track means no decimation."
    ),
  simplify: zod
    .number()
    .optional()
    .describe(
      'Douglas-Peucker tolerance in meters: drop every track point lying closer than this to the line joining the points kept around it. The returned line stays within that many meters of the stored one, and its first and last points are always kept. Capped at 1000; absent or zero means no simplification.'
    ),
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
                    parts: zod
                      .array(
                        zod
                          .object({
                            startDistance: zod
                              .number()
                              .describe('Start distance from route start in meters'),
                            endDistance: zod
                              .number()
                              .describe('End distance from route start in meters'),
                            elevationGain: zod.number().describe('Elevation gain in meters'),
                            grade: zod.number().describe('Gradient percentage'),
                          })
                          .describe('Climb part information')
                      )
                      .describe('Gradient segments making up the climb'),
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
    commentCount: zod
      .number()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    elevationProfile: zod
      .object({
        routeId: zod.string().describe('Route ID (TSID)'),
        slug: zod.string().describe('Route slug'),
        distance: zod.number().describe('Distance covered by the profile, in meters'),
        minElevation: zod.number().describe('Lowest elevation of the profile, in meters'),
        maxElevation: zod.number().describe('Highest elevation of the profile, in meters'),
        samples: zod
          .number()
          .describe(
            'Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.'
          ),
        points: zod
          .array(
            zod
              .object({
                distance: zod
                  .number()
                  .describe('Cumulative distance from the start of the route, in meters'),
                elevation: zod.number().describe('Elevation above sea level, in meters'),
                grade: zod
                  .number()
                  .describe(
                    'Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.'
                  ),
              })
              .describe('One point of an elevation profile')
          )
          .describe('Profile points, by increasing distance'),
      })
      .optional()
      .describe(
        "Sampled elevation profile of the route. Absent unless explicitly requested (the bulk route endpoint's 'elevation' flag) — computing and serialising it costs nothing to skip, so every other caller of this DTO gets exactly what it got before this field existed."
      ),
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
 * The route's elevation profile resampled to 'samples' evenly spaced distances, each point carrying its cumulative distance, its elevation and the grade in percent of the segment ending on it — everything needed to draw a profile coloured by gradient without downloading the full track. Multi-track routes are concatenated into one continuous profile. The answer never holds more points than the stored track.
 * @summary Get route elevation profile
 */
export const GetRouteElevationProfileParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const getRouteElevationProfileQuerySamplesDefault = 300

export const GetRouteElevationProfileQueryParams = zod.object({
  samples: zod
    .number()
    .default(getRouteElevationProfileQuerySamplesDefault)
    .describe(
      'Number of profile points wanted. Clamped server-side to 2..1000, and further reduced to the number of points actually stored for the route.'
    ),
})

export const GetRouteElevationProfileResponse = zod
  .object({
    routeId: zod.string().describe('Route ID (TSID)'),
    slug: zod.string().describe('Route slug'),
    distance: zod.number().describe('Distance covered by the profile, in meters'),
    minElevation: zod.number().describe('Lowest elevation of the profile, in meters'),
    maxElevation: zod.number().describe('Highest elevation of the profile, in meters'),
    samples: zod
      .number()
      .describe(
        'Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.'
      ),
    points: zod
      .array(
        zod
          .object({
            distance: zod
              .number()
              .describe('Cumulative distance from the start of the route, in meters'),
            elevation: zod.number().describe('Elevation above sea level, in meters'),
            grade: zod
              .number()
              .describe(
                'Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.'
              ),
          })
          .describe('One point of an elevation profile')
      )
      .describe('Profile points, by increasing distance'),
  })
  .describe('Sampled elevation profile of a route, ready to draw')

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
                    parts: zod
                      .array(
                        zod
                          .object({
                            startDistance: zod
                              .number()
                              .describe('Start distance from route start in meters'),
                            endDistance: zod
                              .number()
                              .describe('End distance from route start in meters'),
                            elevationGain: zod.number().describe('Elevation gain in meters'),
                            grade: zod.number().describe('Gradient percentage'),
                          })
                          .describe('Climb part information')
                      )
                      .describe('Gradient segments making up the climb'),
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
    commentCount: zod
      .number()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    elevationProfile: zod
      .object({
        routeId: zod.string().describe('Route ID (TSID)'),
        slug: zod.string().describe('Route slug'),
        distance: zod.number().describe('Distance covered by the profile, in meters'),
        minElevation: zod.number().describe('Lowest elevation of the profile, in meters'),
        maxElevation: zod.number().describe('Highest elevation of the profile, in meters'),
        samples: zod
          .number()
          .describe(
            'Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.'
          ),
        points: zod
          .array(
            zod
              .object({
                distance: zod
                  .number()
                  .describe('Cumulative distance from the start of the route, in meters'),
                elevation: zod.number().describe('Elevation above sea level, in meters'),
                grade: zod
                  .number()
                  .describe(
                    'Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.'
                  ),
              })
              .describe('One point of an elevation profile')
          )
          .describe('Profile points, by increasing distance'),
      })
      .optional()
      .describe(
        "Sampled elevation profile of the route. Absent unless explicitly requested (the bulk route endpoint's 'elevation' flag) — computing and serialising it costs nothing to skip, so every other caller of this DTO gets exactly what it got before this field existed."
      ),
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
                    parts: zod
                      .array(
                        zod
                          .object({
                            startDistance: zod
                              .number()
                              .describe('Start distance from route start in meters'),
                            endDistance: zod
                              .number()
                              .describe('End distance from route start in meters'),
                            elevationGain: zod.number().describe('Elevation gain in meters'),
                            grade: zod.number().describe('Gradient percentage'),
                          })
                          .describe('Climb part information')
                      )
                      .describe('Gradient segments making up the climb'),
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
    commentCount: zod
      .number()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    elevationProfile: zod
      .object({
        routeId: zod.string().describe('Route ID (TSID)'),
        slug: zod.string().describe('Route slug'),
        distance: zod.number().describe('Distance covered by the profile, in meters'),
        minElevation: zod.number().describe('Lowest elevation of the profile, in meters'),
        maxElevation: zod.number().describe('Highest elevation of the profile, in meters'),
        samples: zod
          .number()
          .describe(
            'Number of points actually returned. Never more than the number of points stored for the route, so a short track is not artificially upsampled.'
          ),
        points: zod
          .array(
            zod
              .object({
                distance: zod
                  .number()
                  .describe('Cumulative distance from the start of the route, in meters'),
                elevation: zod.number().describe('Elevation above sea level, in meters'),
                grade: zod
                  .number()
                  .describe(
                    'Grade of the segment ending at this point, in percent. Zero on the first point, which ends no segment.'
                  ),
              })
              .describe('One point of an elevation profile')
          )
          .describe('Profile points, by increasing distance'),
      })
      .optional()
      .describe(
        "Sampled elevation profile of the route. Absent unless explicitly requested (the bulk route endpoint's 'elevation' flag) — computing and serialising it costs nothing to skip, so every other caller of this DTO gets exactly what it got before this field existed."
      ),
  })
  .describe('Detailed route information')

/**
 * Rides and trips that reference this route, directly or via a group/stage. Results are visibility filtered for the caller.
 * @summary List route usages
 */
export const GetRouteUsagesParams = zod.object({
  routeSlug: zod.string().describe('Route slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetRouteUsagesResponse = zod
  .object({
    usages: zod
      .array(
        zod
          .object({
            type: zod.enum(['RIDE', 'POST', 'TRIP']).describe('Publication type (RIDE or TRIP)'),
            slug: zod.string().describe('Publication URL slug'),
            name: zod.string().describe('Publication name'),
            dateTime: zod.iso.datetime({ offset: true }).describe('Publication date\/time'),
            teamSlug: zod.string().describe('Slug of the team owning the publication'),
            referencedDirectly: zod
              .boolean()
              .describe(
                'Whether the publication references the route directly (not only via a child)'
              ),
            viaChildNames: zod
              .array(zod.string())
              .describe('Names of the ride groups or trip stages that reference the route, if any'),
          })
          .describe('A ride or trip that uses a route')
      )
      .describe('Usages of the route, most recent first'),
  })
  .describe('Rides and trips that use a route')
