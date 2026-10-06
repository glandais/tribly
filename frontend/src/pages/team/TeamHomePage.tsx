import { useParams, useSearchParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { PublicationListPage } from '../publication/PublicationListPage'
import { TeamDashboardPage } from './TeamDashboardPage'
import { showsTeamDashboard } from './teamHomeData'

/**
 * The team's own URL (`team-detail`): the dashboard for a member of the team, the public feed for
 * anyone else — and for a member who asked for it with `?tab=publications`. The choice reads the
 * team's `role`, which the route's prefetch has already put in the cache (`prefetchTeamHome`), so
 * the server and the client render the same branch.
 */
export function TeamHomePage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [searchParams] = useSearchParams()
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  if (team.isLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (team.data && showsTeamDashboard(team.data, searchParams)) {
    return <TeamDashboardPage team={team.data} />
  }

  // An unknown team or a failure is the feed's to handle: it already tells one from the other.
  return <PublicationListPage />
}
