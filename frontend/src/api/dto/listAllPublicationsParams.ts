import type { MinRole } from './minRole.ts'
import type { PublicationType } from './publicationType.ts'

export type ListAllPublicationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Only publications from teams where the user has at least this role. Yields nothing for an anonymous visitor.
   */
  minRole?: MinRole
  /**
   * Page number
   */
  page?: number
  /**
   * Search by name/markdown
   */
  search?: string
  /**
   * Page size
   */
  size?: number
  /**
   * End date filter (ISO format)
   */
  to?: string
  /**
   * Types
   */
  type?: PublicationType
}
