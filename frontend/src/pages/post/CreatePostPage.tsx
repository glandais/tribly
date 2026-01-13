import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { notifications } from '@mantine/notifications'
import { Container, Stack, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useCreatePost } from '../../api/endpoints/posts/posts'
import { getListPublicationsQueryKey } from '../../api/endpoints/publications/publications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PostEditor } from '../../components/post/PostEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'
import { PostRequest, Visibility, Status } from '../../api/dto'

export function CreatePostPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreatePost()

  useCanonicalPath(team ? paths.postNew(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !teamSlug) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  // Prepare initial values for create mode
  const initialValues: PostRequest = {
    name: '',
    media: defaultMedia(),
    dateTime: new Date().toISOString(),
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
    status: Status.DRAFT,
    publishAt: undefined,
  }

  const handleSubmit = (data: PostRequest) => {
    createMutation.mutate(
      {
        teamSlug: teamSlug!,
        data,
      },
      {
        onSuccess: (post) => {
          queryClient.invalidateQueries({ queryKey: getListPublicationsQueryKey(teamSlug!) })
          notifications.show({ message: t('posts.notifications.created'), color: 'green' })
          navigate(paths.post(teamSlug!, post.slug))
        },
      }
    )
  }

  return (
    <Container size="sm" py="xl">
      <Stack gap="xs">
        <Title order={1}>{t('posts.create.title')}</Title>
      </Stack>

      <PostEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.team(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('posts.create.submit')}
      />
    </Container>
  )
}
