import * as zod from 'zod'

/**
 * Get paginated list of ads for a team with optional filtering
 * @summary List ads
 */
export const ListAdsParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const listAdsQueryPageDefault = 0
export const listAdsQuerySizeDefault = 20

export const ListAdsQueryParams = zod.object({
  adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).optional().describe('Filter by ad type'),
  from: zod.string().optional().describe('Start date filter (ISO format)'),
  maxPrice: zod.number().optional().describe('Highest asking price to include'),
  minPrice: zod
    .number()
    .optional()
    .describe(
      "Lowest asking price to include. Ads with no price ('à négocier') are excluded by either price bound."
    ),
  nearLat: zod.number().optional().describe('Latitude for proximity search'),
  nearLon: zod.number().optional().describe('Longitude for proximity search'),
  nearRadius: zod
    .number()
    .optional()
    .describe(
      'Search radius in metres around nearLat\/nearLon (default 25000, capped at 500000). Ads with no location are excluded when a centre is given.'
    ),
  page: zod.number().default(listAdsQueryPageDefault).describe('Page number'),
  search: zod.string().optional().describe('Search by name\/description'),
  size: zod.number().default(listAdsQuerySizeDefault).describe('Page size'),
  sortBy: zod
    .enum(['DATE_TIME', 'PRICE', 'NAME'])
    .optional()
    .describe('Sort column (default: publication date)'),
  sortDir: zod.enum(['ASC', 'DESC']).optional().describe('Sort direction (default: DESC)'),
  to: zod.string().optional().describe('End date filter (ISO format)'),
  view: zod
    .enum(['FULL', 'COMPACT'])
    .optional()
    .describe(
      "How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets trimmed to the logo, the first image and the themed thumbnails — read 'excerpt', 'thumbnailUrl' and 'images' instead, all of which are present either way. The markdown body, the attachments, the GPX and FIT files and every image past the first are dropped. Omitted, or FULL, is the previous behaviour, byte for byte."
    ),
})

export const ListAdsResponse = zod
  .object({
    ads: zod
      .array(
        zod
          .object({
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
            id: zod.string().describe('Ad ID (TSID)'),
            slug: zod.string().describe('Ad URL slug'),
            name: zod.string().describe('Ad name'),
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
              .describe('Ad media'),
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
                "URL template of the ad's first picture, the one a card shows. Saves a compact row from carrying media.assets just to find it."
              ),
            images: zod
              .array(zod.string())
              .describe(
                "URL templates of every picture on the ad, in editor order — the gallery. Present whatever the 'view', so a compact row can show a carousel without pulling media.assets. The first entry is the same picture as 'thumbnailUrl'."
              ),
            status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
            visibility: zod
              .enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC'])
              .describe('Visibility level'),
            adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
            price: zod.number().optional().describe('Price'),
            rentalPeriod: zod
              .enum(['DAY', 'WEEK', 'MONTH'])
              .optional()
              .describe(
                "Period the price applies to, for a rental — render as 'price \/ period'. Null for a sale, and for a rental whose period has not been set."
              ),
            locationDescription: zod.string().optional().describe('Location description'),
            locationGeometry: zod
              .object({
                type: zod.enum(['Point']),
                coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
              })
              .optional()
              .describe(
                "Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, which only the owner reads."
              ),
            createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
            createdById: zod.string().describe('Creator ID (TSID)'),
            createdByDisplayName: zod
              .string()
              .describe(
                'Display name of the member who posted the ad. The only thing about them this DTO carries: there is no contact channel on an Ad, and inventing one (an email, a phone number) is a product decision, not a serialisation one.'
              ),
            deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
          })
          .describe('Ad data')
      )
      .describe('List of ads'),
    total: zod.number().describe('Total number of ads'),
    page: zod.number().describe('Current page number'),
    size: zod.number().describe('Page size'),
  })
  .describe('Paginated ad list response')

/**
 * Create a new ad. Any team member can create ads.
 * @summary Create ad
 */
export const CreateAdParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const createAdBodyNameMax = 200

export const createAdBodyNameRegExp = new RegExp('\\S')
export const createAdBodyLocationDescriptionMax = 200

export const CreateAdBody = zod
  .object({
    name: zod
      .string()
      .min(1)
      .max(createAdBodyNameMax)
      .regex(createAdBodyNameRegExp)
      .describe('Ad name'),
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
      .describe('Ad description'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe("Price (optional, null for 'contact for price')"),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe('Rental period (required for RENTAL type)'),
    locationDescription: zod
      .string()
      .max(createAdBodyLocationDescriptionMax)
      .optional()
      .describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
  })
  .describe('Ad request')

export const CreateAdResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
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
        "URL template of the ad's first picture, the one a card shows. Saves a compact row from carrying media.assets just to find it."
      ),
    images: zod
      .array(zod.string())
      .describe(
        "URL templates of every picture on the ad, in editor order — the gallery. Present whatever the 'view', so a compact row can show a carousel without pulling media.assets. The first entry is the same picture as 'thumbnailUrl'."
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe(
        "Period the price applies to, for a rental — render as 'price \/ period'. Null for a sale, and for a rental whose period has not been set."
      ),
    locationDescription: zod.string().optional().describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe(
        "Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, which only the owner reads."
      ),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    createdByDisplayName: zod
      .string()
      .describe(
        'Display name of the member who posted the ad. The only thing about them this DTO carries: there is no contact channel on an Ad, and inventing one (an email, a phone number) is a product decision, not a serialisation one.'
      ),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')

/**
 * Update ad information. Only the creator or an admin can update.
 * @summary Update ad
 */
export const UpdateAdParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const updateAdBodyNameMax = 200

export const updateAdBodyNameRegExp = new RegExp('\\S')
export const updateAdBodyLocationDescriptionMax = 200

export const UpdateAdBody = zod
  .object({
    name: zod
      .string()
      .min(1)
      .max(updateAdBodyNameMax)
      .regex(updateAdBodyNameRegExp)
      .describe('Ad name'),
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
      .describe('Ad description'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe("Price (optional, null for 'contact for price')"),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe('Rental period (required for RENTAL type)'),
    locationDescription: zod
      .string()
      .max(updateAdBodyLocationDescriptionMax)
      .optional()
      .describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
  })
  .describe('Ad request')

export const UpdateAdResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
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
        "URL template of the ad's first picture, the one a card shows. Saves a compact row from carrying media.assets just to find it."
      ),
    images: zod
      .array(zod.string())
      .describe(
        "URL templates of every picture on the ad, in editor order — the gallery. Present whatever the 'view', so a compact row can show a carousel without pulling media.assets. The first entry is the same picture as 'thumbnailUrl'."
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe(
        "Period the price applies to, for a rental — render as 'price \/ period'. Null for a sale, and for a rental whose period has not been set."
      ),
    locationDescription: zod.string().optional().describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe(
        "Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, which only the owner reads."
      ),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    createdByDisplayName: zod
      .string()
      .describe(
        'Display name of the member who posted the ad. The only thing about them this DTO carries: there is no contact channel on an Ad, and inventing one (an email, a phone number) is a product decision, not a serialisation one.'
      ),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')

/**
 * Get detailed ad information
 * @summary Get ad details
 */
export const GetAdParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetAdResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
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
        "URL template of the ad's first picture, the one a card shows. Saves a compact row from carrying media.assets just to find it."
      ),
    images: zod
      .array(zod.string())
      .describe(
        "URL templates of every picture on the ad, in editor order — the gallery. Present whatever the 'view', so a compact row can show a carousel without pulling media.assets. The first entry is the same picture as 'thumbnailUrl'."
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe(
        "Period the price applies to, for a rental — render as 'price \/ period'. Null for a sale, and for a rental whose period has not been set."
      ),
    locationDescription: zod.string().optional().describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe(
        "Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, which only the owner reads."
      ),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    createdByDisplayName: zod
      .string()
      .describe(
        'Display name of the member who posted the ad. The only thing about them this DTO carries: there is no contact channel on an Ad, and inventing one (an email, a phone number) is a product decision, not a serialisation one.'
      ),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')

/**
 * Soft delete an ad. Only the creator or an admin can delete.
 * @summary Delete ad
 */
export const DeleteAdParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const DeleteAdResponse = zod.void()

/**
 * Relays a message to the author of an ad. Neither party's address appears anywhere in this API: the server sends the email and sets Reply-To to the caller, so the author can answer directly. Writing therefore discloses the caller's address to the author — a one-way disclosure the caller chose — while the author's address is never disclosed at all. Requires the same access as reading the ad.
 * @summary Contact an ad's author
 */
export const ContactAdAuthorParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const contactAdAuthorBodyMessageMin = 10
export const contactAdAuthorBodyMessageMax = 2000

export const contactAdAuthorBodyMessageRegExp = new RegExp('\\S')

export const ContactAdAuthorBody = zod
  .object({
    message: zod
      .string()
      .min(contactAdAuthorBodyMessageMin)
      .max(contactAdAuthorBodyMessageMax)
      .regex(contactAdAuthorBodyMessageRegExp)
      .describe(
        'The message body, plain text. Rendered as text in the email: any markup is escaped rather than interpreted.'
      ),
  })
  .describe('A message to relay to the author of an ad')

export const ContactAdAuthorResponse = zod.void()

/**
 * Get detailed ad information
 * @summary Get ad details for edit
 */
export const GetAdEditParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const GetAdEditResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod.enum(['DAY', 'WEEK', 'MONTH']).optional().describe('Rental period'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    locationDescription: zod.string().optional().describe('Location description'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')

/**
 * Change ad URL slug. Requires admin permissions.
 * @summary Change ad slug
 */
export const ChangeAdSlugParams = zod.object({
  slug: zod.string().describe('Current ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const changeAdSlugBodySlugMax = 200

export const changeAdSlugBodySlugRegExp = new RegExp('^[a-z0-9]+(-[a-z0-9]+)*$')

export const ChangeAdSlugBody = zod
  .object({
    slug: zod
      .string()
      .max(changeAdSlugBodySlugMax)
      .regex(changeAdSlugBodySlugRegExp)
      .describe('New slug (lowercase letters, numbers, and hyphens only)'),
  })
  .describe('Slug change request')

export const ChangeAdSlugResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
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
        "URL template of the ad's first picture, the one a card shows. Saves a compact row from carrying media.assets just to find it."
      ),
    images: zod
      .array(zod.string())
      .describe(
        "URL templates of every picture on the ad, in editor order — the gallery. Present whatever the 'view', so a compact row can show a carousel without pulling media.assets. The first entry is the same picture as 'thumbnailUrl'."
      ),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod
      .enum(['DAY', 'WEEK', 'MONTH'])
      .optional()
      .describe(
        "Period the price applies to, for a rental — render as 'price \/ period'. Null for a sale, and for a rental whose period has not been set."
      ),
    locationDescription: zod.string().optional().describe('Location description'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe(
        "Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, which only the owner reads."
      ),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    createdByDisplayName: zod
      .string()
      .describe(
        'Display name of the member who posted the ad. The only thing about them this DTO carries: there is no contact channel on an Ad, and inventing one (an email, a phone number) is a product decision, not a serialisation one.'
      ),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')

/**
 * Restore a soft-deleted ad. Only the creator or an admin can restore.
 * @summary Restore ad
 */
export const UndeleteAdParams = zod.object({
  slug: zod.string().describe('Ad URL slug'),
  teamSlug: zod.string().describe('Team URL slug'),
})

export const UndeleteAdResponse = zod
  .object({
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
    id: zod.string().describe('Ad ID (TSID)'),
    slug: zod.string().describe('Ad URL slug'),
    name: zod.string().describe('Ad name'),
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
      .describe('Ad media'),
    status: zod.enum(['DRAFT', 'PUBLISHED', 'CANCELLED']).describe('Ad status'),
    visibility: zod.enum(['TEAM', 'PUBLIC_UNLISTED', 'PUBLIC']).describe('Visibility level'),
    adType: zod.enum(['SALE', 'RENTAL', 'WANTED']).describe('Ad type'),
    price: zod.number().optional().describe('Price'),
    rentalPeriod: zod.enum(['DAY', 'WEEK', 'MONTH']).optional().describe('Rental period'),
    locationGeometry: zod
      .object({
        type: zod.enum(['Point']),
        coordinates: zod.array(zod.number()).describe('Coordinates [longitude, latitude]'),
      })
      .optional()
      .describe('Location coordinates [longitude, latitude]'),
    locationDescription: zod.string().optional().describe('Location description'),
    createdAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    updatedAt: zod.iso.datetime({ offset: true }).describe('Creation timestamp'),
    createdById: zod.string().describe('Creator ID (TSID)'),
    deleted: zod.boolean().describe('Whether the ad is soft-deleted'),
  })
  .describe('Ad data')
