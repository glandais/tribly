import { useParams, useNavigate, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import {
  useGetRoute,
  useUpdateRoute,
  useChangeRouteSlug,
  getGetRouteQueryKey,
  getListRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { Visibility, SurfaceType, RouteRequest } from '@/api/dto'
import { LoadingPage } from '@/components/common/LoadingSpinner'
import { RouteEditor } from '@/components/route/RouteEditor'

export function EditRoutePage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: route, isLoading } = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  const updateRouteMutation = useUpdateRoute()
  const changeSlugMutation = useChangeRouteSlug()

  useCanonicalPath(team && route ? paths.routeEdit(team.slug, route.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('routes.create.title')} />
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
    surfaceType: route.surfaceType || SurfaceType.ROAD,
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : route.visibility,
  }

  // Check if route has a single track (required for planner mode)
  const isSingleTrack = route.tracks.length === 1
  const initialTrack = isSingleTrack ? route.tracks[0].line.coordinates : undefined

  const handleSubmit = async (data: RouteRequest, gpxFile?: File) => {
    await updateRouteMutation.mutateAsync(
      {
        slug: teamSlug!,
        routeSlug: routeSlug!,
        data: {
          route: data,
          gpxFile,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getGetRouteQueryKey(teamSlug!, routeSlug!) })
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug!) })
          toast.success(i18next.t('routes.notifications.updated'))
        },
      }
    )
    navigate(paths.route(teamSlug!, routeSlug!))
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { slug: teamSlug!, routeSlug: routeSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedRoute) => {
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetRouteQueryKey(teamSlug!, routeSlug!) })
          navigate(paths.routeEdit(teamSlug!, updatedRoute.slug), { replace: true })
        },
      }
    )
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('routes.edit.title')}</h1>
        <p className="mt-2 text-gray-600">{t('routes.edit.subtitle')}</p>
      </div>

      <RouteEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        initialTrack={initialTrack}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.route(teamSlug!, routeSlug!))}
        isPending={updateRouteMutation.isPending}
        error={updateRouteMutation.error}
        submitButtonText={t('actions.save')}
        currentSlug={routeSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={canEdit}
      />
    </div>
  )
}
