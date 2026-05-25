import type { MediaDto } from './mediaDto.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Team page detail
 */
export interface TeamPageDto {
  /** Team */
  team: TeamPublicationDto
  /** Page ID (TSID) */
  id: string
  /** Page title */
  title: string
  /** Page URL slug */
  slug: string
  /** Page content */
  media: MediaDto
  /** Visibility level */
  visibility: Visibility
  /** Page order */
  order: number
  /** Whether the page is soft-deleted */
  deleted: boolean
}
