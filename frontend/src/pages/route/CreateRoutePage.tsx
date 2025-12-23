import { useParams, Link, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RouteForm } from '../../components/route/RouteForm'
import type { RouteDto } from '../../api/api'

export function CreateRoutePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { t } = useTranslation('routes')

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const handleSuccess = (route: RouteDto) => {
    navigate(`/teams/${teamSlug}/routes/${route.slug}`)
  }

  const handleCancel = () => {
    navigate(`/teams/${teamSlug}/routes`)
  }

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToList')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-2 text-gray-600">{t('create.subtitle')}</p>
      </div>

      <RouteForm
        teamSlug={teamSlug!}
        teamVisibility={team.visibility}
        onSuccess={handleSuccess}
        onCancel={handleCancel}
        submitButtonText={t('create.submit')}
      />
    </div>
  )
}
