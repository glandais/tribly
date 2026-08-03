import { useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { paths } from '../../config/paths'
import { useTeamAdminData } from './teamAdminData'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { useTranslation } from 'react-i18next'

export function TeamAdminPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()

  const { data: team, isLoading } = useTeamAdminData(teamSlug)

  useEffect(() => {
    if (isLoading) return

    if (!team) {
      navigate(paths.teams(), { replace: true })
      return
    }

    const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'
    if (!isOrganizer) {
      navigate(paths.team(teamSlug!), { replace: true })
      return
    }

    // Redirect to ride templates as the default admin tab
    navigate(paths.rideTemplates(teamSlug!), { replace: true })
  }, [team, isLoading, teamSlug, navigate])

  return <LoadingPage message={t('loading')} />
}
