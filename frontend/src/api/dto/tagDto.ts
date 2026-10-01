import type { TagColor } from './tagColor.ts'

/**
 * A team tag on a content
 */
export interface TagDto {
  /** Tag ID (TSID) */
  id: string
  /** Label, at most 32 characters */
  label: string
  /** Colour family */
  color: TagColor
}
