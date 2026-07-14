export type SocialProvider = (typeof SocialProvider)[keyof typeof SocialProvider]

export const SocialProvider = {
  STRAVA: 'STRAVA',
} as const
