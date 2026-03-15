/**
 * Public user information (limited fields)
 */
export interface PublicUserDto {
  /** User ID (TSID) */
  id: string
  /** User display name */
  displayName: string
  /** User avatar URL */
  avatarUrl?: string
}
