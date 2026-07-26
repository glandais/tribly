import type { MinRole } from './minRole.ts'

export type ListTeamsParams = {
  /**
   * Keep only teams that accept a join request from any domain user (true), or only those that do not (false). Omitted keeps both. A filter on top of the visibility rules, never instead of them.
   */
  joinable?: boolean
  /**
   * Minimum role in team
   */
  minRole?: MinRole
  /**
   * Page number (0-indexed)
   */
  page?: number
  /**
   * Search query to filter teams by name
   */
  search?: string
  /**
   * Page size
   */
  size?: number
}
