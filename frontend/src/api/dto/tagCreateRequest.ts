import type { TagColor } from './tagColor.ts'
import type { TagTarget } from './tagTarget.ts'

/**
 * Tag creation request
 */
export interface TagCreateRequest {
  /** Kind of content the tag applies to */
  type: TagTarget
  /**
   * Label, trimmed; unique in the team and kind whatever the case, at most 32 characters once trimmed (TAG_LABEL_INVALID otherwise)
   * @maxLength 255
   */
  label: string
  /** Colour family */
  color: TagColor
}
