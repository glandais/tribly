import type { Visibility } from './visibility'

/**
 * Team page summary for listings
 */
export interface TeamPageSummaryDto {
  /** Page ID (TSID) */
  id: string
  /** Page title */
  title: string
  /** Page URL slug */
  slug: string
  /** Visibility level */
  visibility: Visibility
  /** Page order */
  order: number
  /** Whether the page is soft-deleted */
  deleted: boolean
}
