import type { AdType } from './adType'

export type ListAdsParams = {
  /**
   * Filter by ad type
   */
  adType?: AdType
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Page number
   */
  page?: number
  /**
   * Search by name/description
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
}
