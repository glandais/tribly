import { useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { paths } from '../../config/paths'
import { useTeamAdminData } from './teamAdminData'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { useTranslation } from 'react-i18next'
import { useAuthStore, selectIsPlatformAdmin } from '@/store/authStore'

export function TeamAdminPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()

  const { data: team, isLoading } = useTeamAdminData(teamSlug)
  const isPlatformAdmin = useAuthStore(selectIsPlatformAdmin)

  useEffect(() => {
    if (isLoading) return

    if (!team) {
      navigate(paths.teams(), { replace: true })
      return
    }

    const isOrganizer = team.role === 'ADMIN' || team.role === 'ORGANIZER'
    // A platform admin who is not an organizer here can only moderate the team.
    if (!isOrganizer && isPlatformAdmin) {
      navigate(paths.teamAdminReports(teamSlug!), { replace: true })
      return
    }
    if (!isOrganizer) {
      navigate(paths.team(teamSlug!), { replace: true })
      return
    }

    // Redirect to ride templates as the default admin tab
    navigate(paths.rideTemplates(teamSlug!), { replace: true })
  }, [team, isLoading, teamSlug, navigate, isPlatformAdmin])

  return <LoadingPage message={t('loading')} />
}
