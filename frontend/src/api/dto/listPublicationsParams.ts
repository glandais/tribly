import type { ListViewMode } from './listViewMode.ts'
import type { PublicationType } from './publicationType.ts'
import type { Status } from './status.ts'

export type ListPublicationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Page number
   */
  page?: number
  /**
   * Only publications the current user is registered to (rides and trips). Yields nothing for an anonymous visitor.
   */
  participating?: boolean
  /**
   * Search by name/markdown
   */
  search?: string
  /**
   * Page size
   */
  size?: number
  /**
   * Only publications with this status. Narrows the visibility rules, never widens them.
   */
  status?: Status
  /**
   * End date filter (ISO format)
   */
  to?: string
  /**
   * Type
   */
  type?: PublicationType
  /**
   * How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. Omitted, or FULL, is the previous behaviour, byte for byte.
   */
  view?: ListViewMode
}
