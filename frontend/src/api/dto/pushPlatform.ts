export type PushPlatform = (typeof PushPlatform)[keyof typeof PushPlatform]

export const PushPlatform = {
  ANDROID: 'ANDROID',
  IOS: 'IOS',
} as const
