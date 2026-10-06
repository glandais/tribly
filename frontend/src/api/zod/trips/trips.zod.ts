import * as zod from 'zod'

/**
 * Create a new trip with optional stages
 * @summary Create trip
 */
export const CreateTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createTripBodyNameMax = 200

export const createTripBodyNameRegExp = new RegExp('\\S')
export const createTripBodyMediaMarkdownMax = 100000

export const createTripBodyStagesItemNameMax = 200

export const createTripBodyStagesItemNameRegExp = new RegExp('\\S')
export const createTripBodyStagesItemAverageSpeedExclusiveMin = 0

export const createTripBodyStagesItemMediaMarkdownMax = 100000

export const CreateTripBody = zod
  .object({
    name: zod
      .string()
      .min(1)
      .max(createTripBodyNameMax)
      .regex(createTripBodyNameRegExp)
      .describe('Trip name'),
    media: zod
      .object({
        markdown: zod.string().max(createTripBodyMediaMarkdownMax).describe('Markdown'),
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
      .describe('Trip media'),
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Trip status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Overall route slug for the trip'),
    publishAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Publication timestamp (for scheduled publishing)'),
    stages: zod
      .array(
        zod
          .object({
            id: zod.string().optional().describe('Stage ID (for updates)'),
            name: zod
              .string()
              .min(1)
              .max(createTripBodyStagesItemNameMax)
              .regex(createTripBodyStagesItemNameRegExp)
              .describe('Stage name'),
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod
              .number()
              .gt(createTripBodyStagesItemAverageSpeedExclusiveMin)
              .optional()
              .describe('Average speed in km/h'),
            routeSlug: zod.string().optional().describe('Route slug for this stage'),
            startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
            endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
            media: zod
              .object({
                markdown: zod
                  .string()
                  .max(createTripBodyStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
          })
          .describe('Trip stage creation request')
      )
      .describe('Trip stages to create'),
    tagIds: zod
      .array(zod.string())
      .optional()
      .describe(
        "IDs (TSID) of the team's TRIP tags the trip carries, replacing the whole set — at most 10, each a tag of this team and of kind TRIP, else 400 (TAG_INVALID, TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a creation, left as they are on an update."
      ),
  })
  .describe('Trip request')

export const createTripResponseMediaMarkdownMax = 100000

export const createTripResponseStagesItemRouteMediaMarkdownMax = 100000

export const createTripResponseStagesItemMediaMarkdownMax = 100000

export const CreateTripResponse = zod
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
        markdown: zod.string().max(createTripResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(createTripResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(createTripResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')

/**
 * Update trip information. Requires organizer permissions.
 * @summary Update trip
 */
export const UpdateTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const updateTripBodyNameMax = 200

export const updateTripBodyNameRegExp = new RegExp('\\S')
export const updateTripBodyMediaMarkdownMax = 100000

export const updateTripBodyStagesItemNameMax = 200

export const updateTripBodyStagesItemNameRegExp = new RegExp('\\S')
export const updateTripBodyStagesItemAverageSpeedExclusiveMin = 0

export const updateTripBodyStagesItemMediaMarkdownMax = 100000

export const UpdateTripBody = zod
  .object({
    name: zod
      .string()
      .min(1)
      .max(updateTripBodyNameMax)
      .regex(updateTripBodyNameRegExp)
      .describe('Trip name'),
    media: zod
      .object({
        markdown: zod.string().max(updateTripBodyMediaMarkdownMax).describe('Markdown'),
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
      .describe('Trip media'),
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Trip status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    routeSlug: zod.string().optional().describe('Overall route slug for the trip'),
    publishAt: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe('Publication timestamp (for scheduled publishing)'),
    stages: zod
      .array(
        zod
          .object({
            id: zod.string().optional().describe('Stage ID (for updates)'),
            name: zod
              .string()
              .min(1)
              .max(updateTripBodyStagesItemNameMax)
              .regex(updateTripBodyStagesItemNameRegExp)
              .describe('Stage name'),
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod
              .number()
              .gt(updateTripBodyStagesItemAverageSpeedExclusiveMin)
              .optional()
              .describe('Average speed in km/h'),
            routeSlug: zod.string().optional().describe('Route slug for this stage'),
            startPlaceId: zod.string().optional().describe('Start place ID (TSID)'),
            endPlaceId: zod.string().optional().describe('End place ID (TSID)'),
            media: zod
              .object({
                markdown: zod
                  .string()
                  .max(updateTripBodyStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
          })
          .describe('Trip stage creation request')
      )
      .describe('Trip stages to create'),
    tagIds: zod
      .array(zod.string())
      .optional()
      .describe(
        "IDs (TSID) of the team's TRIP tags the trip carries, replacing the whole set — at most 10, each a tag of this team and of kind TRIP, else 400 (TAG_INVALID, TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a creation, left as they are on an update."
      ),
  })
  .describe('Trip request')

export const updateTripResponseMediaMarkdownMax = 100000

export const updateTripResponseStagesItemRouteMediaMarkdownMax = 100000

export const updateTripResponseStagesItemMediaMarkdownMax = 100000

export const UpdateTripResponse = zod
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
        markdown: zod.string().max(updateTripResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(updateTripResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(updateTripResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')

/**
 * Get detailed trip information including stages and participants
 * @summary Get trip details
 */
export const GetTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const getTripResponseMediaMarkdownMax = 100000

export const getTripResponseStagesItemRouteMediaMarkdownMax = 100000

export const getTripResponseStagesItemMediaMarkdownMax = 100000

export const GetTripResponse = zod
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
        markdown: zod.string().max(getTripResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(getTripResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(getTripResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')

/**
 * Soft delete a trip. Requires organizer permissions.
 * @summary Delete trip
 */
export const DeleteTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const DeleteTripResponse = zod.void()

/**
 * One all-day VEVENT per stage, to add the trip on its own to a calendar. Readable by whoever may read the trip; no calendar token.
 * @summary Download trip as a calendar file
 */
export const DownloadTripIcsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const DownloadTripIcsResponse = zod.unknown()

/**
 * Join a trip as a participant
 * @summary Join trip
 */
export const JoinTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const JoinTripResponse = zod
  .object({
    id: zod.string().describe('Participation ID (TSID)'),
    userId: zod.string().describe('User ID (TSID)'),
    registeredAt: zod.iso.datetime({ offset: true }).optional().describe('Registration timestamp'),
  })
  .describe('Trip participation information')

/**
 * Leave a trip as a participant
 * @summary Leave trip
 */
export const LeaveTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const LeaveTripResponse = zod.void()

/**
 * One page of the people registered to the trip, earliest registrations first, searchable by display name. The trip detail only embeds the first few; this is the whole list, with its total. Readable by whoever may read the trip.
 * @summary List trip participants
 */
export const GetTripParticipantsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const getTripParticipantsQueryPageDefault = 0
export const getTripParticipantsQuerySizeDefault = 50

export const GetTripParticipantsQueryParams = zod.object({
  page: zod.int().default(getTripParticipantsQueryPageDefault).describe('Page number (0-based)'),
  search: zod.string().optional().describe('Search by display name'),
  size: zod.int().default(getTripParticipantsQuerySizeDefault).describe('Page size'),
})

export const GetTripParticipantsResponse = zod
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
 * Change trip URL slug. Requires organizer permissions.
 * @summary Change trip slug
 */
export const ChangeTripSlugParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Current trip URL slug'),
})

export const changeTripSlugBodySlugMax = 200

export const changeTripSlugBodySlugRegExp = new RegExp('^[a-z0-9]+(-[a-z0-9]+)*$')

export const ChangeTripSlugBody = zod
  .object({
    slug: zod
      .string()
      .max(changeTripSlugBodySlugMax)
      .regex(changeTripSlugBodySlugRegExp)
      .describe('New slug (lowercase letters, numbers, and hyphens only)'),
  })
  .describe('Slug change request')

export const changeTripSlugResponseMediaMarkdownMax = 100000

export const changeTripSlugResponseStagesItemRouteMediaMarkdownMax = 100000

export const changeTripSlugResponseStagesItemMediaMarkdownMax = 100000

export const ChangeTripSlugResponse = zod
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
        markdown: zod.string().max(changeTripSlugResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(changeTripSlugResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(changeTripSlugResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')

/**
 * Change the trip's status and nothing else — what a list row can do without the full trip. Same side effects as a status change through the update. Requires organizer permissions. The stages follow the trip.
 * @summary Change trip status
 */
export const ChangeTripStatusParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const ChangeTripStatusBody = zod
  .object({
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('New status'),
  })
  .describe('Status change request')

export const changeTripStatusResponseMediaMarkdownMax = 100000

export const changeTripStatusResponseStagesItemRouteMediaMarkdownMax = 100000

export const changeTripStatusResponseStagesItemMediaMarkdownMax = 100000

export const ChangeTripStatusResponse = zod
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
        markdown: zod.string().max(changeTripStatusResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(changeTripStatusResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(changeTripStatusResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')

/**
 * Restore a soft-deleted trip. Requires organizer permissions.
 * @summary Restore trip
 */
export const UndeleteTripParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
  tripSlug: zod.string().describe('Trip URL slug'),
})

export const undeleteTripResponseMediaMarkdownMax = 100000

export const undeleteTripResponseStagesItemRouteMediaMarkdownMax = 100000

export const undeleteTripResponseStagesItemMediaMarkdownMax = 100000

export const UndeleteTripResponse = zod
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
        markdown: zod.string().max(undeleteTripResponseMediaMarkdownMax).describe('Markdown'),
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
    dateTime: zod.iso.datetime({ offset: true }).describe('Trip start date/time'),
    endDate: zod.iso
      .datetime({ offset: true })
      .optional()
      .describe(
        'Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends.'
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Publication status'),
    finished: zod
      .boolean()
      .describe(
        'Whether the trip is over, computed by the server when the response is built: its last stage (endDate, or dateTime when there is none) has started. Independent of status — a past cancelled trip is both CANCELLED and finished.'
      ),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    publishAt: zod.iso.datetime({ offset: true }).optional().describe('Publication timestamp'),
    createdAt: zod.iso.datetime({ offset: true }).optional().describe('Creation timestamp'),
    routeSlug: zod.string().optional().describe('Route slug'),
    participantCount: zod.int().describe('Number of participants'),
    stageCount: zod.int().describe('Number of stages'),
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
            dateTime: zod.iso.datetime({ offset: true }).describe('Stage date/time'),
            averageSpeed: zod.number().optional().describe('Average speed in km/h'),
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
                    logoUrl: zod
                      .string()
                      .optional()
                      .describe(
                        "URL template of the team's logo (with a {size} placeholder), when it has one. Same value as TeamDetailDto.logoUrl, so a publication can show its team's logo without loading the team."
                      ),
                  })
                  .describe('Team'),
                name: zod.string().describe('Route name'),
                media: zod
                  .object({
                    markdown: zod
                      .string()
                      .max(undeleteTripResponseStagesItemRouteMediaMarkdownMax)
                      .describe('Markdown'),
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
                  .int()
                  .optional()
                  .describe(
                    'Number of comments, replies included. Absent when the caller may not read the comments of this route — comments are members-only, so an outsider is told nothing, not even zero.'
                  ),
                tags: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Tag ID (TSID)'),
                        label: zod.string().describe('Label, at most 32 characters'),
                        color: zod
                          .enum([
                            'INDIGO',
                            'BLUE',
                            'GREEN',
                            'RED',
                            'YELLOW',
                            'ORANGE',
                            'GRAPE',
                            'TEAL',
                            'GRAY',
                          ])
                          .describe('Colour family'),
                      })
                      .describe('A team tag on a content')
                  )
                  .describe(
                    "The team's ROUTE tags the route carries, sorted by label. Empty when it carries none."
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
                markdown: zod
                  .string()
                  .max(undeleteTripResponseStagesItemMediaMarkdownMax)
                  .describe('Markdown'),
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
              .describe('Stage media'),
            sortOrder: zod.int().describe('Sort order'),
            stageIndex: zod
              .int()
              .describe(
                "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
              ),
            stageCount: zod
              .int()
              .describe("How many live stages the trip has — the '/ 5' of 'Day 2 / 5'."),
            commentCount: zod
              .int()
              .optional()
              .describe(
                "Number of comments on the stage's own thread. Absent when the caller cannot read comments (not a member of the team), like TripDto.commentCount."
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
      .describe(
        'The first participants of the trip (at most 8), earliest registrations first — enough to draw avatars; empty on a list row. participantCount is the total; the whole list is paginated and searched by GET …/trips/{tripSlug}/participants.'
      ),
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
      .describe('Whether the current user is registered for this trip. False if anonymous.'),
    commentCount: zod
      .int()
      .optional()
      .describe(
        'Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero.'
      ),
    tags: zod
      .array(
        zod
          .object({
            id: zod.string().describe('Tag ID (TSID)'),
            label: zod.string().describe('Label, at most 32 characters'),
            color: zod
              .enum(['INDIGO', 'BLUE', 'GREEN', 'RED', 'YELLOW', 'ORANGE', 'GRAPE', 'TEAL', 'GRAY'])
              .describe('Colour family'),
          })
          .describe('A team tag on a content')
      )
      .describe(
        "The team's TRIP tags the trip carries, sorted by label. Empty when it carries none."
      ),
  })
  .describe('Trip data')
