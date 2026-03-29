import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { Container, Stack, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetPost,
  useUpdatePost,
  useChangePostSlug,
  getGetPostQueryKey,
} from '../../api/endpoints/posts/posts'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PostEditor } from '../../components/post/PostEditor'
import { paths } from '@/config/paths'
import { PostRequest } from '@/api/dto'

export function EditPostPage() {
  const { t } = useTranslation()
  const { teamSlug, postSlug } = useParams<{ teamSlug: string; postSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: post, isLoading: isLoadingPost } = useGetPost(teamSlug!, postSlug!, {
    query: { enabled: !!teamSlug && !!postSlug },
  })

  const updateMutation = useUpdatePost()
  const changeSlugMutation = useChangePostSlug()

  useCanonicalPath(team && post ? paths.postEdit(team.slug, post.slug) : undefined)

  if (isLoadingTeam || isLoadingPost) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !post) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  if (!team.enablePosts) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.post(teamSlug!, postSlug!)} replace />
  }

  const handleSubmit = (data: PostRequest) => {
    updateMutation.mutate(
      {
        teamSlug: teamSlug!,
        postSlug: postSlug!,
        data,
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
          notifications.show({ message: t('posts.notifications.updated'), color: 'green' })
          navigate(paths.post(teamSlug!, postSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { teamSlug: teamSlug!, postSlug: postSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedPost) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
          navigate(paths.postEdit(teamSlug!, updatedPost.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched post data
  const initialValues: PostRequest = { ...post }

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('posts.edit.title')}</Title>
      </Stack>

      <PostEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.post(teamSlug!, postSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
        currentSlug={postSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={canEdit}
      />
    </Container>
  )
}
