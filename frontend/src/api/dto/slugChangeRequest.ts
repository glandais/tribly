/**
 * Slug change request
 */
export interface SlugChangeRequest {
  /**
   * New slug (lowercase letters, numbers, and hyphens only)
   * @maxLength 200
   * @pattern ^[a-z0-9]+(-[a-z0-9]+)*$
   */
  slug: string
}
