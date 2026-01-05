import { useParams, Navigate } from 'react-router-dom'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { useTranslation } from 'react-i18next'

export function TeamAdminPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { data: team, isLoading } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  if (isLoading) {
    return <LoadingPage message={t('loading')} />
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
