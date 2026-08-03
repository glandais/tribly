import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { Box, Button, Group, Stack, Title } from '@mantine/core'
import { useDeleteRoute, useUndeleteRoute } from '@/api/endpoints/routes/routes'
import { invalidateRouteQueries } from '@/lib/routeCacheInvalidation'
import { useRouteDetailData } from './routeDetailData'
import { RouteDetailView } from '../../components/route/RouteDetailView'
import { RouteUsages } from '../../components/route/RouteUsages'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { QueryStateBoundary } from '../../components/common/QueryStateBoundary'
import { DetailPageSkeleton } from '../../components/common/DetailPageSkeleton'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RouteDetailPage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { team: teamQuery, route: routeQuery } = useRouteDetailData(teamSlug, routeSlug)
  const { data: team, isLoading: teamLoading } = teamQuery
  const { data: route, isLoading: routeLoading, error, refetch } = routeQuery
  const deleteRouteMutation = useDeleteRoute()
  const undeleteRouteMutation = useUndeleteRoute()

  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  useCanonicalPath(team && route ? paths.route(team.slug, route.slug) : undefined)

  const isMember = !!team?.role
  const canEdit = team && (team.role === 'ADMIN' || team.role === 'ORGANIZER')

  const handleDelete = async () => {
    if (routeSlug && teamSlug) {
      await deleteRouteMutation.mutateAsync(
        { teamSlug: teamSlug, routeSlug },
        {
          onSuccess: () => {
            invalidateRouteQueries(queryClient, teamSlug, routeSlug)
            notifications.show({
              message: i18next.t('routes.notifications.deleted'),
              color: 'green',
            })
            navigate(paths.routes(teamSlug))
          },
        }
      )
    }
  }

  const handleRestore = async () => {
    if (routeSlug && teamSlug) {
      await undeleteRouteMutation.mutateAsync(
        { teamSlug: teamSlug, routeSlug },
        {
          onSuccess: () => {
            invalidateRouteQueries(queryClient, teamSlug, routeSlug)
            notifications.show({
              message: i18next.t('routes.notifications.restored'),
              color: 'green',
            })
          },
        }
      )
    }
  }

  if (routeLoading || teamLoading || error || !route || !team) {
    return (
      <Box maw={1280} mx="auto" px="md" py="xl">
        <QueryStateBoundary
          isLoading={routeLoading || teamLoading}
          isError={!!error}
          error={error}
          isNotFound={!route || !team}
          onRetry={() => void refetch()}
          skeleton={<DetailPageSkeleton />}
          notFound={{
            title: t('routes.detail.notFound.title'),
            message: t('routes.detail.notFound.message'),
            backTo: paths.routes(teamSlug!),
            backLabel: t('routes.detail.notFound.backToRoutes'),
          }}
        >
          {null}
        </QueryStateBoundary>
      </Box>
    )
  }

  return (
    <Box maw={1280} mx="auto" px="md" py="xl">
      {/* Header */}
      <Stack mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Stack gap="xs">
            <Group>
              <EntityLogo logo={route.media.assets.logo} alt={route.name} size="lg" />
              <Title order={1}>{route.name}</Title>
            </Group>
            <MediaDisplay media={route.media} />
          </Stack>
          {canEdit && (
            <Group mt={{ base: 'md', sm: 0 }}>
              <Button variant="default" component="a" href={paths.routeEdit(teamSlug!, routeSlug!)}>
                {t('actions.edit')}
              </Button>
              {route.deleted && (
                <Button
                  variant="outline"
                  color="green"
                  onClick={handleRestore}
                  loading={undeleteRouteMutation.isPending}
                >
                  {t('actions.restore')}
                </Button>
              )}
              <Button variant="outline" color="danger" onClick={() => setShowDeleteConfirm(true)}>
                {t('actions.delete')}
              </Button>
            </Group>
          )}
        </Group>
      </Stack>

      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('routes.detail.deleteConfirm.title')}
        message={t('routes.detail.deleteConfirm.message')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteRouteMutation.isPending}
      />

      {/* Route Details */}
      <Box>
        <RouteDetailView
          route={route}
          teamSlug={teamSlug}
          fullscreenPath={paths.routeMap(team.slug, route.slug)}
        />
      </Box>

      {/* Where this route is used (rides & trips) — visible to all, results already access-filtered */}
      <RouteUsages teamSlug={teamSlug!} routeSlug={routeSlug!} />

      {/* Comments Section - only visible to team members */}
      {isMember && (
        <CommentSection
          teamSlug={teamSlug!}
          entityType="routes"
          entitySlug={routeSlug!}
          isOrganizer={!!canEdit}
        />
      )}
    </Box>
  )
}
