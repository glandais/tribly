import type { UserDto } from './userDto.ts'

/**
 * Authentication response
 */
export interface AuthResponse {
  /** JWT access token */
  accessToken?: string
  /** Token expiry in seconds */
  expiresIn?: number
  /** Authenticated user */
  user?: UserDto
  /** Refresh token (for mobile clients) */
  refreshToken?: string
}
