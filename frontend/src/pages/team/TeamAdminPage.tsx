import { useParams, Navigate } from 'react-router-dom'
import { paths } from '../../config/paths'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { useTranslation } from 'react-i18next'

export function TeamAdminPage() {
  const { t } = useTranslation('teams')
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useTeam(teamSlug)

  if (isLoading) {
    return <LoadingPage message={t('detail.loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  // Check if user has admin access
  const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!isOrganizer) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  // Redirect to ride templates as the default admin tab
  return <Navigate to={paths.rideTemplates(teamSlug!)} replace />
}
