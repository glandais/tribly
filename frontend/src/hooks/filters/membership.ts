import { z } from 'zod'
import { MinRole } from '@/api/dto'

/** Restrict a listing to the teams the user belongs to, with at least a given role. */
export type MembershipFilterValue = 'all' | 'member' | 'organizer' | 'admin'

export const membershipToMinRole: Record<MembershipFilterValue, MinRole | undefined> = {
  all: undefined,
  member: MinRole.MEMBER,
  organizer: MinRole.ORGANIZER,
  admin: MinRole.ADMIN,
}

/** The default is context-dependent (auth, and whether the user has any team), hence a factory. */
export const membershipField = (defaultValue: MembershipFilterValue) =>
  z.enum(['all', 'member', 'organizer', 'admin']).default(defaultValue).catch(defaultValue)

export const MEMBERSHIP_ALIAS = { membership: 'role' } as const

/**
 * Always written to the URL: two visitors resolve the default differently, so a shared link
 * must spell the membership out.
 */
export const MEMBERSHIP_ALWAYS_SERIALIZE = ['membership'] as const
