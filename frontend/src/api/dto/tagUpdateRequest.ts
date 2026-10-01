import type { TagColor } from './tagColor.ts'

/**
 * Tag update request — absent fields are unchanged
 */
export interface TagUpdateRequest {
  /**
   * New label, trimmed, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
   * @maxLength 255
   */
  label?: string
  /** New colour family */
  color?: TagColor
}
