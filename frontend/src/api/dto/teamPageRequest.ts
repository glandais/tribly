import type { MediaDto } from './mediaDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Team page request
 */
export interface TeamPageRequest {
  /**
   * Page title
   * @minLength 1
   * @maxLength 100
   * @pattern \S
   */
  title: string
  /** Page content */
  media: MediaDto
  /** Visibility level */
  visibility: Visibility
}
