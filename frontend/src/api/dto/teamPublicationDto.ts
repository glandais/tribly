import type { Visibility } from './visibility'

/**
 * Team information
 */
export interface TeamPublicationDto {
  /** Team ID (TSID) */
  id: string
  /** Team name */
  name: string
  /** Team URL slug */
  slug: string
  /** Whether the team is public */
  visibility: Visibility
}
