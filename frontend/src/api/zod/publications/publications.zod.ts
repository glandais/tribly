import * as zod from 'zod'

/**
 * Get publications from all accessible teams (user's teams + public teams)
 * @summary List all publications
 */
export const listAllPublicationsQueryPageDefault = 0
export const listAllPublicationsQueryParticipatingDefault = false
export const listAllPublicationsQuerySizeDefault = 20

export const ListAllPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only publications from teams where the user has at least this role. Yields nothing for an anonymous visitor.'
    ),
  page: zod.int().default(listAllPublicationsQueryPageDefault).describe('Page number'),
  participating: zod
    .boolean()
    .default(listAllPublicationsQueryParticipatingDefault)
    .describe(
      'Only publications the current user is registered to (rides and trips). Yields nothing for an anonymous visitor.'
    ),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.int().default(listAllPublicationsQuerySizeDefault).describe('Page size'),
  status: zod
    .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
    .optional()
    .describe(
      'Only publications with this status. Narrows the visibility rules, never widens them.'
    ),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Types'),
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                        averageSpeed: zod.number().optional().describe('Average speed in km\/h'),
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
                          .describe('Participants, empty if not access'),
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
                  .int()
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                  .int()
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                              width: zod.int().optional(),
                                              height: zod.int().optional(),
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
                                              width: zod.int().optional(),
                                              height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                              .int()
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                          width: zod.int().optional(),
                                          height: zod.int().optional(),
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
                                          width: zod.int().optional(),
                                          height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                        sortOrder: zod.int().describe('Sort order'),
                        stageIndex: zod
                          .int()
                          .describe(
                            "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
                          ),
                        stageCount: zod
                          .int()
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
                  .int()
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
    total: zod.int().describe('Total number of publications'),
    page: zod.int().describe('Current page number'),
    size: zod.int().describe('Page size'),
  })
  .describe('Paginated publication list response')

/**
 * How many publications match the filters, with none of them read. Accepts exactly the same filters as the listing, minus pagination, so a count and the list it opens can never disagree.
 * @summary Count all publications
 */
export const countAllPublicationsQueryParticipatingDefault = false

export const CountAllPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  minRole: zod
    .enum(['MEMBER', 'ORGANIZER', 'ADMIN'])
    .optional()
    .describe(
      'Only publications from teams where the user has at least this role. Yields zero for an anonymous visitor.'
    ),
  participating: zod
    .boolean()
    .default(countAllPublicationsQueryParticipatingDefault)
    .describe(
      'Only publications the current user is registered to (rides and trips). Yields zero for an anonymous visitor.'
    ),
  search: zod.string().optional().describe('Search by name\/markdown'),
  status: zod
    .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
    .optional()
    .describe(
      'Only publications with this status. Narrows the visibility rules, never widens them.'
    ),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Types'),
})

export const CountAllPublicationsResponse = zod
  .object({
    total: zod.int().describe('Total number of matching items'),
  })
  .describe('Number of items matching a filter set')

/**
 * Get paginated list of publications for a team with optional filtering
 * @summary List publications
 */
export const ListPublicationsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const listPublicationsQueryPageDefault = 0
export const listPublicationsQueryParticipatingDefault = false
export const listPublicationsQuerySizeDefault = 20

export const ListPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  page: zod.int().default(listPublicationsQueryPageDefault).describe('Page number'),
  participating: zod
    .boolean()
    .default(listPublicationsQueryParticipatingDefault)
    .describe(
      'Only publications the current user is registered to (rides and trips). Yields nothing for an anonymous visitor.'
    ),
  search: zod.string().optional().describe('Search by name\/markdown'),
  size: zod.int().default(listPublicationsQuerySizeDefault).describe('Page size'),
  status: zod
    .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
    .optional()
    .describe(
      'Only publications with this status. Narrows the visibility rules, never widens them.'
    ),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Type'),
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                        averageSpeed: zod.number().optional().describe('Average speed in km\/h'),
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
                          .describe('Participants, empty if not access'),
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
                  .int()
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                  .int()
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                  width: zod.int().optional(),
                                  height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                width: zod.int().optional(),
                                height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                              width: zod.int().optional(),
                                              height: zod.int().optional(),
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
                                              width: zod.int().optional(),
                                              height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                                            width: zod.int().optional(),
                                            height: zod.int().optional(),
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
                              .int()
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                          width: zod.int().optional(),
                                          height: zod.int().optional(),
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
                                          width: zod.int().optional(),
                                          height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                                        width: zod.int().optional(),
                                        height: zod.int().optional(),
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
                        sortOrder: zod.int().describe('Sort order'),
                        stageIndex: zod
                          .int()
                          .describe(
                            "Position of this stage among the trip's live stages, 1-based — the 'Day 2' of a stage header. Unlike sortOrder, which is a persisted rank that may have gaps, this is a rank a client can print."
                          ),
                        stageCount: zod
                          .int()
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
                  .int()
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
    total: zod.int().describe('Total number of publications'),
    page: zod.int().describe('Current page number'),
    size: zod.int().describe('Page size'),
  })
  .describe('Paginated publication list response')

/**
 * How many of the team's publications match the filters, with none of them read. Accepts exactly the same filters as the listing, minus pagination, so a count and the list it opens can never disagree.
 * @summary Count publications
 */
export const CountPublicationsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const countPublicationsQueryParticipatingDefault = false

export const CountPublicationsQueryParams = zod.object({
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  participating: zod
    .boolean()
    .default(countPublicationsQueryParticipatingDefault)
    .describe(
      'Only publications the current user is registered to (rides and trips). Yields zero for an anonymous visitor.'
    ),
  search: zod.string().optional().describe('Search by name\/markdown'),
  status: zod
    .enum(['DRAFT', 'PUBLISHED', 'CANCELLED'])
    .optional()
    .describe(
      'Only publications with this status. Narrows the visibility rules, never widens them.'
    ),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  type: zod.enum(['RIDE', 'POST', 'TRIP']).optional().describe('Type'),
})

export const CountPublicationsResponse = zod
  .object({
    total: zod.int().describe('Total number of matching items'),
  })
  .describe('Number of items matching a filter set')
