import { useAuth } from '../../hooks/useAuth'
import { MemberHome } from './MemberHome'
import { VisitorHome } from './VisitorHome'

/**
 * The home page: the visitor's (pitch, sign-in form, public feed) or the member's (next ride, the
 * week, their teams, then the feed). The split follows the session the server rendered with —
 * `isAuthenticated` is the same on the SSR pass and on the hydration render — and both read their
 * data through `homeFeedData.ts`, which the `home` route prefetches.
 */
export function HomePage() {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? <MemberHome /> : <VisitorHome />
}
