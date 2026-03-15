import type { MediaDto } from './mediaDto'
import type { Visibility } from './visibility'

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
