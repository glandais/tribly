import type { Instant } from './instant'
import type { Visibility } from './visibility'

/**
 * Admin team view with domain info
 */
export interface AdminTeamDto {
  /** Team ID (TSID) */
  id: string
  /** Team name */
  name: string
  /** Team URL slug */
  slug: string
  /** Domain ID this team belongs to */
  domainId: string
  /** Domain hostname */
  domainName: string
  /** Team visibility */
  visibility: Visibility
  /** Is team soft-deleted */
  deleted: boolean
  /** Number of members */
  memberCount: number
  /** Team creation timestamp */
  createdAt: Instant
}
