import { useParams, useNavigate, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
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
    return <Navigate to={paths.teams()} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.routes(teamSlug!)} replace />
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
    return <Navigate to={paths.routes(teamSlug!)} replace />
  }

  // Prepare initial values from fetched route data
  const initialValues = {
    name: route.name,
    media: route.media,
    surfaceType: route.surfaceType || SurfaceType.Road,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : route.visibility,
  }

  // Check if route has a single track (required for planner mode)
  const isSingleTrack = route.tracks.length === 1
  const initialTrack = isSingleTrack ? route.tracks[0].line.coordinates : undefined

  const handleSubmit = async (data: RouteFormData, gpxFile?: File) => {
    await updateRoute.mutateAsync({
      route: {
        name: data.name,
        media: data.media,
        surfaceType: data.surfaceType,
        visibility: data.visibility,
        points: data.points,
      },
      gpxFile,
    })
    navigate(paths.route(teamSlug!, routeSlug!))
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
        <p className="mt-2 text-gray-600">{t('edit.subtitle')}</p>
      </div>

      <RouteEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        initialTrack={initialTrack}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.route(teamSlug!, routeSlug!))}
        isPending={updateRoute.isPending}
        error={updateRoute.error}
        submitButtonText={t('edit.submit')}
      />
    </div>
  )
}
