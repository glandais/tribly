export type GpsOAuthVersion = (typeof GpsOAuthVersion)[keyof typeof GpsOAuthVersion]

export const GpsOAuthVersion = {
  OAUTH1: 'OAUTH1',
  OAUTH2: 'OAUTH2',
} as const
