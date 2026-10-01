/**
 * Result of a tag deletion
 */
export interface TagDeletedDto {
  /** Contents the tag was detached from, counted like usageCount (trashed contents lose it too, uncounted) */
  detachedCount: number
}
