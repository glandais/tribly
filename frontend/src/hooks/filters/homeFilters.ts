import { COMMON_ALIAS } from './common'
import {
  MEMBERSHIP_ALIAS,
  MEMBERSHIP_ALWAYS_SERIALIZE,
  membershipField,
  type MembershipFilterValue,
} from './membership'
import { publicationFiltersSchema } from './publicationFilters'

/** The home feed is the cross-team publication list, so it adds a membership filter. */
export const makeHomeFiltersSchema = (defaultMembership: MembershipFilterValue) =>
  publicationFiltersSchema.extend({ membership: membershipField(defaultMembership) })

export const homeFiltersAlias = {
  ...COMMON_ALIAS,
  ...MEMBERSHIP_ALIAS,
  filter: 'type',
} as const

export const homeFiltersAlwaysSerialize = MEMBERSHIP_ALWAYS_SERIALIZE
