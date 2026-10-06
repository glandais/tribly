export type PublicationWhen = (typeof PublicationWhen)[keyof typeof PublicationWhen]

export const PublicationWhen = {
  UPCOMING: 'UPCOMING',
  PAST: 'PAST',
} as const
