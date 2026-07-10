import { useListTeams } from '@/api/endpoints/teams/teams'
import { MinRole } from '@/api/dto'
import type { MembershipFilterValue } from './filters/membership'
import { useAuth } from './useAuth'

/**
 * The membership filter a list page should start on.
 *
 * Signed in, the user wants their own teams. But defaulting a user who belongs to no team to
 * `member` would greet them with an empty page, so probe for a single membership and fall back
 * to `all`. The probe is optimistic while it loads — most users do have a team — which avoids a
 * flash of unfiltered content for them.
 */
export function useMembershipDefault(): MembershipFilterValue {
  const { isAuthenticated } = useAuth()
  const { data } = useListTeams(
    { minRole: MinRole.MEMBER, page: 0, size: 1 },
    { query: { enabled: isAuthenticated } }
  )

  if (!isAuthenticated) return 'all'
  if (data && data.total === 0) return 'all'
  return 'member'
}
