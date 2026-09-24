import type { ModerationItemDto } from './moderationItemDto.ts'

/**
 * A moderation queue: every open target, or the 100 most recently decided ones, one item per target
 */
export interface ModerationQueueResponse {
  /** One item per reported target */
  items: ModerationItemDto[]
  /** How many items */
  total: number
}
