export type SearchUsersParams = {
  /**
   * Maximum results (max 20)
   */
  limit?: number
  /**
   * Search query
   */
  q?: string
  /**
   * Keep only members of this team
   */
  teamSlug?: string
}
