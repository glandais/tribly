import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { Box, Button, Group, SimpleGrid, Skeleton, Stack, Text, Title, Center } from '@mantine/core'
import { useGetRoute, useDeleteRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { RouteDetailView } from '../../components/route/RouteDetailView'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RouteDetailPage() {
  const { teamSlug, routeSlug } = useParams<{ teamSlug: string; routeSlug: string }>()
  const { t } = useTranslation()
  const navigate = useNavigate()
  const queryClient = useQueryClient()

  const { data: team } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: route, isLoading: routeLoading } = useGetRoute(teamSlug!, routeSlug!, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })
  const deleteRouteMutation = useDeleteRoute()

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
            queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug) })
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

  if (routeLoading) {
    return (
      <Box maw={1280} mx="auto" px="md" py="xl">
        <Stack gap="md">
          <Skeleton height={32} width="25%" />
          <Skeleton height={384} />
          <SimpleGrid cols={{ base: 1, md: 3 }} spacing="lg">
            {[...Array(3)].map((_, i) => (
              <Skeleton key={i} height={96} />
            ))}
          </SimpleGrid>
        </Stack>
      </Box>
    )
  }

  if (!route || !team) {
    return (
      <Box maw={1280} mx="auto" px="md" py="xl">
        <Center>
          <Text c="dimmed">{t('errors.api.notFound')}</Text>
        </Center>
      </Box>
    )
  }

  return (
    <Box maw={1280} mx="auto" px="md" py="xl">
      {/* Header */}
      <Stack gap="lg" mb="lg">
        <Group justify="space-between" align="flex-start" wrap="wrap">
          <Stack gap="xs">
            <Group gap="md">
              <EntityLogo logo={route.media.assets.logo} alt={route.name} size="lg" />
              <Title order={1}>{route.name}</Title>
            </Group>
            <MediaDisplay media={route.media} />
          </Stack>
          {canEdit && (
            <Group gap="md" mt={{ base: 'md', sm: 0 }}>
              <Button variant="default" component="a" href={paths.routeEdit(teamSlug!, routeSlug!)}>
                {t('actions.edit')}
              </Button>
              <Button variant="outline" color="red" onClick={() => setShowDeleteConfirm(true)}>
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
      <Box mb="xl">
        <RouteDetailView route={route} />
      </Box>

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
