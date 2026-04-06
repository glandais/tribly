import * as zod from 'zod'

/**
 * Get publications from all accessible teams (user's teams + public teams)
 * @summary List all publications
 */
export const listAllPublicationsQueryPageDefault = 0
export const listAllPublicationsQuerySizeDefault = 20

export const ListAllPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  page: zod.number().default(listAllPublicationsQueryPageDefault).describe('Page number'),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.number().default(listAllPublicationsQuerySizeDefault).describe('Page size'),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Types'),
})

export const ListAllPublicationsResponse = zod
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
                dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
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
                deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
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
                dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
                createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
                deleted: zod.boolean().describe('Whether the post is soft-deleted'),
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
                dateTime: zod.iso.datetime({}).describe('Trip start date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
                createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
                routeSlug: zod.string().optional().describe('Route slug'),
                participantCount: zod.number().describe('Number of participants'),
                stageCount: zod.number().describe('Number of stages'),
                stages: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Stage ID (TSID)'),
                        slug: zod.string().describe('Stage slug'),
                        name: zod.string().describe('Stage name'),
                        dateTime: zod.iso.datetime({}).describe('Stage date\/time'),
                        routeSlug: zod.string().optional().describe('Route slug'),
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
                deleted: zod.boolean().describe('Whether the trip is soft-deleted'),
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
 * Get paginated list of publications for a team with optional filtering
 * @summary List publications
 */
export const ListPublicationsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const listPublicationsQueryPageDefault = 0
export const listPublicationsQuerySizeDefault = 20

export const ListPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  page: zod.number().default(listPublicationsQueryPageDefault).describe('Page number'),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.number().default(listPublicationsQuerySizeDefault).describe('Page size'),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Type'),
})

export const ListPublicationsResponse = zod
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
                dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
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
                deleted: zod.boolean().describe('Whether the ride is soft-deleted'),
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
                dateTime: zod.iso.datetime({}).describe('Publication date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
                createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
                deleted: zod.boolean().describe('Whether the post is soft-deleted'),
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
                dateTime: zod.iso.datetime({}).describe('Trip start date\/time'),
                status: zod
                  .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
                  .describe('Publication status'),
                visibility: zod
                  .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
                  .describe('Visibility level'),
                publishAt: zod.iso.datetime({}).optional().describe('Publication timestamp'),
                createdAt: zod.iso.datetime({}).optional().describe('Creation timestamp'),
                routeSlug: zod.string().optional().describe('Route slug'),
                participantCount: zod.number().describe('Number of participants'),
                stageCount: zod.number().describe('Number of stages'),
                stages: zod
                  .array(
                    zod
                      .object({
                        id: zod.string().describe('Stage ID (TSID)'),
                        slug: zod.string().describe('Stage slug'),
                        name: zod.string().describe('Stage name'),
                        dateTime: zod.iso.datetime({}).describe('Stage date\/time'),
                        routeSlug: zod.string().optional().describe('Route slug'),
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
                deleted: zod.boolean().describe('Whether the trip is soft-deleted'),
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
