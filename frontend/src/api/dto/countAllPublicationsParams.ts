import type { MinRole } from './minRole.ts'
import type { PublicationType } from './publicationType.ts'
import type { Status } from './status.ts'

export type CountAllPublicationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Only publications from teams where the user has at least this role. Yields zero for an anonymous visitor.
   */
  minRole?: MinRole
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
   * End date filter (ISO format)
   */
  to?: string
  /**
   * Types
   */
  type?: PublicationType
}
