import type { ThumbnailFile } from './thumbnailFile.ts'
import type { ThumbnailOwnerKind } from './thumbnailOwnerKind.ts'
import type { ThumbnailRegenerationOutcome } from './thumbnailRegenerationOutcome.ts'
import type { ThumbnailRegenerationReason } from './thumbnailRegenerationReason.ts'

/**
 * A route, ride or trip whose thumbnails were (or would be) redrawn
 */
export interface ThumbnailOwnerReport {
  /** Entity ID */
  id: string
  /** Entity kind */
  kind: ThumbnailOwnerKind
  /** Team slug */
  teamSlug: string
  /** Entity slug */
  slug: string
  /** Why it was selected */
  reasons: ThumbnailRegenerationReason[]
  /** Its thumbnails before */
  before: ThumbnailFile[]
  /** Its thumbnails after, absent on a dry run */
  after?: ThumbnailFile[]
  /** What happened */
  outcome: ThumbnailRegenerationOutcome
  /** Error message when the regeneration itself failed */
  error?: string
}
