import type { ThumbnailOwnerReport } from './thumbnailOwnerReport.ts'

/**
 * Outcome of a thumbnail regeneration, one entry per entity
 */
export interface ThumbnailRegenerationResponse {
  /** Whether this was a dry run (nothing redrawn) */
  dryRun: boolean
  /** Number of entities matching the criteria, before the limit */
  matched: number
  /** Entities processed, at most the limit */
  entities: ThumbnailOwnerReport[]
}
