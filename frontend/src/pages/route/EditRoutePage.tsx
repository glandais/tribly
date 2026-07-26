import { useParams, useNavigate, Navigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { Box, Skeleton, Stack, Text, Title } from '@mantine/core'
import {
  useGetRoute,
  useUpdateRoute,
  useChangeRouteSlug,
  getGetRouteQueryKey,
  getListRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { SurfaceType, RouteRequest } from '@/api/dto'
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
  const { data: route, isLoading } = useGetRoute(teamSlug!, routeSlug!, undefined, {
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

  if (!team.enableRoutes) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.routes(teamSlug!)} replace />
  }

  if (isLoading) {
    return (
      <Box maw={768} mx="auto" px="md" py="xl">
        <Stack>
          <Skeleton height={32} width="25%" />
          <Skeleton height={16} width="50%" />
          <Stack>
            {[...Array(5)].map((_, i) => (
              <Stack key={i} gap="xs">
                <Skeleton height={16} width="25%" />
                <Skeleton height={40} />
              </Stack>
            ))}
          </Stack>
        </Stack>
      </Box>
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
    visibility: route.visibility,
  }

  // Check if route has a single track (required for planner mode)
  const isSingleTrack = route.tracks.length === 1
  const initialTrack = isSingleTrack ? route.tracks[0].line.coordinates : undefined

  const handleSubmit = async (data: RouteRequest, gpxFile?: File) => {
    await updateRouteMutation.mutateAsync(
      {
        teamSlug: teamSlug!,
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
          notifications.show({ message: i18next.t('routes.notifications.updated'), color: 'green' })
        },
      }
    )
    navigate(paths.route(teamSlug!, routeSlug!))
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { teamSlug: teamSlug!, routeSlug: routeSlug!, data: { slug: newSlug } },
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
    <Stack>
      <Stack gap="xs">
        <Title order={1}>{t('routes.edit.title')}</Title>
        <Text c="dimmed">{t('routes.edit.subtitle')}</Text>
      </Stack>

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
    </Stack>
  )
}
