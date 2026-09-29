export type SitemapEntryType = (typeof SitemapEntryType)[keyof typeof SitemapEntryType]

export const SitemapEntryType = {
  TEAM: 'TEAM',
  TEAM_ABOUT: 'TEAM_ABOUT',
  TEAM_PAGE: 'TEAM_PAGE',
  RIDE: 'RIDE',
  POST: 'POST',
  TRIP: 'TRIP',
  TRIP_STAGE: 'TRIP_STAGE',
} as const
