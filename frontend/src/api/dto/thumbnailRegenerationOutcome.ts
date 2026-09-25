export type ThumbnailRegenerationOutcome =
  (typeof ThumbnailRegenerationOutcome)[keyof typeof ThumbnailRegenerationOutcome]

export const ThumbnailRegenerationOutcome = {
  PENDING: 'PENDING',
  REGENERATED: 'REGENERATED',
  FAILED: 'FAILED',
} as const
