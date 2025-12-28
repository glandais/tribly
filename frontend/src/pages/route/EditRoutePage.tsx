import { useParams, Link, useNavigate, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { SurfaceType, useRoute, useUpdateRoute } from '../../hooks/useRoute'
import { useTeam } from '../../hooks/useTeam'
import { Visibility } from '../../api/api'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { RouteEditor } from '@/components/route/RouteEditor'
import type { RouteFormData } from '@/components/route/RouteEditor'

export function EditRoutePage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation('routes')
  const navigate = useNavigate()

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: route, isLoading } = useRoute(teamSlug, routeSlug)
  const updateRoute = useUpdateRoute(teamSlug!, routeSlug!)

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={`/teams/${teamSlug}/routes`} replace />
  }

  if (isLoading) {
    return (
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="animate-pulse">
          <div className="h-8 bg-gray-200 rounded-sm w-1/4 mb-4" />
          <div className="h-4 bg-gray-200 rounded-sm w-1/2 mb-8" />
          <div className="space-y-6">
            {[...Array(5)].map((_, i) => (
              <div key={i}>
                <div className="h-4 bg-gray-200 rounded-sm w-1/4 mb-2" />
                <div className="h-10 bg-gray-200 rounded-sm" />
              </div>
            ))}
          </div>
        </div>
      </div>
    )
  }

  if (!route) {
    return <Navigate to={`/teams/${teamSlug}/routes`} replace />
  }

  // Prepare initial values from fetched route data
  const initialValues = {
    name: route.name,
    media: route.media,
    surfaceType: route.surfaceType || SurfaceType.Road,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : route.visibility,
  }

  const handleSubmit = async (data: RouteFormData, gpxFile?: File) => {
    await updateRoute.mutateAsync({
      route: {
        name: data.name,
        media: data.media,
        surfaceType: data.surfaceType,
        visibility: data.visibility,
      },
      gpxFile,
    })
    navigate(`/teams/${teamSlug}/routes/${routeSlug}`)
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes/${routeSlug}`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('edit.backToDetail')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-2 text-gray-600">{t('edit.subtitle')}</p>
      </div>

      <RouteEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        requireGpxFile={false}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/routes/${routeSlug}`)}
        isPending={updateRoute.isPending}
        error={updateRoute.error}
        submitButtonText={t('edit.submit')}
      />
    </div>
  )
}
