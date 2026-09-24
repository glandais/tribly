import type { PublicUserDto } from './publicUserDto.ts'

/**
 * The members the current user blocked, most recent first
 */
export interface BlockedUsersResponse {
  /** Blocked members */
  users: PublicUserDto[]
}
