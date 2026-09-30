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
  /** Refresh token, for mobile clients. On a refresh, the rotated token — absent when the refresh came within the grace of a rotation made by another one, whose token stands. */
  refreshToken?: string
}
