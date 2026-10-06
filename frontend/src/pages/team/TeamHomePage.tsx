import { useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { TeamUnavailable } from '@/components/team/TeamUnavailable'
import { TeamDashboardPage } from './TeamDashboardPage'

/**
 * The team's own URL (`team-detail`): the dashboard, for everyone (API-86) — a visitor reads its
 * public part, a member their own blocks on top. The former feed is gone (ledger WEB-68); its
 * addresses are redirected by the route's loader before this page renders.
 */
export function TeamHomePage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const team = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  if (team.isLoading) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team.data) {
    return (
      <TeamUnavailable
        error={team.error}
        isError={team.isError}
        onRetry={() => void team.refetch()}
      />
    )
  }

  return <TeamDashboardPage team={team.data} />
}
