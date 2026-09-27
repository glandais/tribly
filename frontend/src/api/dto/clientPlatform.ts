export type ClientPlatform = (typeof ClientPlatform)[keyof typeof ClientPlatform]

export const ClientPlatform = {
  WEB: 'WEB',
  ANDROID: 'ANDROID',
  IOS: 'IOS',
} as const
