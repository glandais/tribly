import type { TagColor } from './tagColor.ts'
import type { TagTarget } from './tagTarget.ts'

/**
 * A team tag with its usage count
 */
export interface TagWithUsageDto {
  /** Tag ID (TSID) */
  id: string
  /** Label, at most 32 characters */
  label: string
  /** Colour family */
  color: TagColor
  /** Kind of content the tag applies to */
  type: TagTarget
  /** Contents carrying the tag: rides, posts, trips, routes and ads out of the trash, plus ride templates — what a deletion would detach it from */
  usageCount: number
}
