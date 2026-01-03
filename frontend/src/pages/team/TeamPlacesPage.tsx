import { useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamAdminLayout } from '../../components/team/TeamAdminLayout'
import { PlaceList } from '../../components/team/PlaceList'

export function TeamPlacesPage() {
  const { t } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useTeam(teamSlug)

  if (isLoading) {
    return <LoadingPage message={t('detail.loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canManage = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamAdminLayout team={team} currentTab="places">
      <div className="py-6">
        <PlaceList teamSlug={teamSlug!} canManage={canManage} />
      </div>
    </TeamAdminLayout>
  )
}
