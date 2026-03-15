export type PlatformRole = (typeof PlatformRole)[keyof typeof PlatformRole]

export const PlatformRole = {
  PLATFORM_ADMIN: 'PLATFORM_ADMIN',
} as const
