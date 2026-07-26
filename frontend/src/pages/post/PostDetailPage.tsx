import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import i18next from 'i18next'
import { IconCalendar, IconPencil, IconChevronDown } from '@tabler/icons-react'
import {
  Button,
  Menu,
  Container,
  Paper,
  Group,
  Title,
  Badge,
  Stack,
  Text,
  Box,
  Loader,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetPost,
  useUpdatePost,
  useDeletePost,
  useUndeletePost,
  getGetPostQueryKey,
} from '../../api/endpoints/posts/posts'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { Status } from '../../api/dto'
import { QueryStateBoundary } from '../../components/common/QueryStateBoundary'
import { DetailPageSkeleton } from '../../components/common/DetailPageSkeleton'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { CommentSection } from '../../components/comment'
import { useFormattedDate } from '../../utils/dateFormat'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const statusColors: Record<Status, 'gray' | 'green' | 'red'> = {
  [Status.DRAFT]: 'gray',
  [Status.PUBLISHED]: 'green',
  [Status.CANCELLED]: 'red',
}

export function PostDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, postSlug } = useParams<{ teamSlug: string; postSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: post,
    isLoading: isLoadingPost,
    error,
    refetch,
  } = useGetPost(teamSlug!, postSlug!, { query: { enabled: !!teamSlug && !!postSlug } })

  const updateMutation = useUpdatePost()
  const deleteMutation = useDeletePost()
  const undeleteMutation = useUndeletePost()

  useCanonicalPath(team && post ? paths.post(team.slug, post.slug) : undefined)

  if (isLoadingTeam || isLoadingPost || error || !post || !team) {
    return (
      <Container size="lg" py="xl">
        <QueryStateBoundary
          isLoading={isLoadingTeam || isLoadingPost}
          isError={!!error}
          error={error}
          isNotFound={!post || !team}
          onRetry={() => void refetch()}
          skeleton={<DetailPageSkeleton withMap={false} />}
          notFound={{
            title: t('posts.detail.notFound.title'),
            message: t('posts.detail.notFound.message'),
            backTo: paths.team(teamSlug!),
            backLabel: t('posts.title'),
          }}
        >
          {null}
        </QueryStateBoundary>
      </Container>
    )
  }

  const isMember = !!team?.role
  const isAdmin = team?.role === 'ADMIN'
  const isOrganizer = team?.role === 'ORGANIZER'
  const canEdit = isAdmin || isOrganizer

  const formattedDate = formatDateTime(post.dateTime)

  const invalidatePosts = () => {
    queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
    queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
  }

  const handlePublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidatePosts()
          notifications.show({ message: i18next.t('posts.notifications.updated'), color: 'green' })
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.DRAFT } },
      {
        onSuccess: () => {
          invalidatePosts()
          notifications.show({ message: i18next.t('posts.notifications.updated'), color: 'green' })
          setShowUnpublishConfirm(false)
        },
      }
    )
  }

  const handleCancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.CANCELLED } },
      {
        onSuccess: () => {
          invalidatePosts()
          notifications.show({ message: i18next.t('posts.notifications.updated'), color: 'green' })
          setShowCancelConfirm(false)
        },
      }
    )
  }

  const handleUncancel = () => {
    updateMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug!, data: { ...post, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidatePosts()
          notifications.show({ message: i18next.t('posts.notifications.updated'), color: 'green' })
          setShowUncancelConfirm(false)
        },
      }
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: i18next.t('posts.notifications.deleted'), color: 'green' })
          setShowDeleteConfirm(false)
          navigate(paths.team(teamSlug!))
        },
      }
    )
  }

  const handleRestore = () => {
    undeleteMutation.mutate(
      { teamSlug: teamSlug!, postSlug: postSlug! },
      {
        onSuccess: () => {
          invalidatePosts()
          notifications.show({ message: i18next.t('posts.notifications.restored'), color: 'green' })
        },
      }
    )
  }

  return (
    <Container size="md" py="xl">
      <Stack>
        {/* Header */}
        <Paper withBorder p="lg" radius="md">
          <Group justify="space-between" align="flex-start" wrap="wrap">
            <Group gap="sm" style={{ minWidth: 0 }}>
              <EntityLogo logo={post.media.assets.logo} alt={post.name} size="lg" />
              <Title order={2} lineClamp={1}>
                {post.name}
              </Title>
              <Badge color={statusColors[post.status]}>
                {t(`status.${post.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
              </Badge>
            </Group>

            {canEdit && (
              <Button.Group>
                <Button
                  component={Link}
                  to={paths.postEdit(teamSlug!, postSlug!)}
                  variant="outline"
                  leftSection={<IconPencil size={16} />}
                >
                  {t('actions.edit')}
                </Button>
                <Menu position="bottom-end">
                  <Menu.Target>
                    <Button variant="outline" px="xs">
                      <IconChevronDown size={16} />
                    </Button>
                  </Menu.Target>
                  <Menu.Dropdown>
                    {post.status === Status.DRAFT && (
                      <Menu.Item
                        onClick={handlePublish}
                        disabled={updateMutation.isPending}
                        color="success"
                        leftSection={updateMutation.isPending ? <Loader size="sm" /> : undefined}
                      >
                        {t('actions.publish')}
                      </Menu.Item>
                    )}
                    {post.status === Status.PUBLISHED && (
                      <>
                        <Menu.Item onClick={() => setShowUnpublishConfirm(true)} color="warning">
                          {t('actions.unpublish')}
                        </Menu.Item>
                        <Menu.Item onClick={() => setShowCancelConfirm(true)} color="warning">
                          {t('actions.cancelAction')}
                        </Menu.Item>
                      </>
                    )}
                    {post.status === Status.CANCELLED && (
                      <Menu.Item onClick={() => setShowUncancelConfirm(true)} color="green">
                        {t('posts.detail.actions.uncancel')}
                      </Menu.Item>
                    )}
                    {post.deleted && (
                      <Menu.Item
                        onClick={handleRestore}
                        color="green"
                        disabled={undeleteMutation.isPending}
                      >
                        {t('actions.restore')}
                      </Menu.Item>
                    )}
                    <Menu.Divider />
                    <Menu.Item onClick={() => setShowDeleteConfirm(true)} color="danger">
                      {t('actions.delete')}
                    </Menu.Item>
                  </Menu.Dropdown>
                </Menu>
              </Button.Group>
            )}
          </Group>

          <Box mt="md">
            <MediaDisplay media={post.media} />
          </Box>
          {post.status === Status.DRAFT && post.publishAt && (
            <Group gap="xs" mt="sm">
              <IconCalendar size={16} color="var(--mantine-color-yellow-text)" />
              <Text size="sm" c="var(--mantine-color-yellow-text)">
                {t('posts.detail.scheduledPublish', {
                  date: formatDateTime(post.publishAt),
                })}
              </Text>
            </Group>
          )}
          <Group mt="md">
            <Group gap="xs">
              <IconCalendar size={16} color="var(--mantine-color-dimmed)" />
              <Text size="sm" c="dimmed">
                {formattedDate}
              </Text>
            </Group>
          </Group>
        </Paper>

        {/* Comments Section - only visible to team members */}
        {isMember && (
          <CommentSection
            teamSlug={teamSlug!}
            entityType="posts"
            entitySlug={postSlug!}
            isOrganizer={canEdit}
          />
        )}

        {/* Confirmation Dialogs */}
        <ConfirmDialog
          isOpen={showUnpublishConfirm}
          onClose={() => setShowUnpublishConfirm(false)}
          onConfirm={handleUnpublish}
          title={t('actions.unpublish')}
          message={t('posts.detail.confirmations.unpublish')}
          confirmText={t('actions.unpublish')}
          variant="warning"
          isLoading={updateMutation.isPending}
        />
        <ConfirmDialog
          isOpen={showCancelConfirm}
          onClose={() => setShowCancelConfirm(false)}
          onConfirm={handleCancel}
          title={t('actions.cancelAction')}
          message={t('posts.detail.confirmations.cancel')}
          confirmText={t('actions.cancelAction')}
          variant="warning"
          isLoading={updateMutation.isPending}
        />
        <ConfirmDialog
          isOpen={showUncancelConfirm}
          onClose={() => setShowUncancelConfirm(false)}
          onConfirm={handleUncancel}
          title={t('posts.detail.actions.uncancel')}
          message={t('posts.detail.confirmations.uncancel')}
          confirmText={t('posts.detail.actions.uncancel')}
          variant="info"
          isLoading={updateMutation.isPending}
        />
        <ConfirmDialog
          isOpen={showDeleteConfirm}
          onClose={() => setShowDeleteConfirm(false)}
          onConfirm={handleDelete}
          title={t('actions.delete')}
          message={t('posts.detail.confirmations.delete')}
          confirmText={t('actions.delete')}
          variant="danger"
          isLoading={deleteMutation.isPending}
        />
      </Stack>
    </Container>
  )
}
