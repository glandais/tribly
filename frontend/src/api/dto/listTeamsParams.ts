import type { MinRole } from './minRole'

export type ListTeamsParams = {
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
