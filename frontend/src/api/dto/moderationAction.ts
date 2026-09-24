export type ModerationAction = (typeof ModerationAction)[keyof typeof ModerationAction]

export const ModerationAction = {
  REMOVE_CONTENT: 'REMOVE_CONTENT',
  DISMISS: 'DISMISS',
} as const
