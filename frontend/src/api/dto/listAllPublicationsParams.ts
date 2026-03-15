import type { PublicationType } from './publicationType'

export type ListAllPublicationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
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
