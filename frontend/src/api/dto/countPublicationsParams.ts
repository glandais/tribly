import type { PublicationType } from './publicationType.ts'
import type { Status } from './status.ts'

export type CountPublicationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Only publications the current user is registered to (rides and trips). Yields zero for an anonymous visitor.
   */
  participating?: boolean
  /**
   * Search by name/markdown
   */
  search?: string
  /**
   * Only publications with this status. Narrows the visibility rules, never widens them.
   */
  status?: Status
  /**
   * Only the publications carrying at least one of these tags — ids (TSID) of the team's tags of kind 'type', comma-separated or repeated. Honoured with a 'type' only: the mixed feed has no tag filter and ignores it. Unknown ids are ignored; a filter left with no known id filters nothing.
   */
  tags?: string[]
  /**
   * End date filter (ISO format)
   */
  to?: string
  /**
   * Type
   */
  type?: PublicationType
}
