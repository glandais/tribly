import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetPost,
  useUpdatePost,
  useChangePostSlug,
  getListPostsQueryKey,
  getGetPostQueryKey,
} from '../../api/endpoints/posts/posts'
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

  if (isLoadingTeam || isLoadingPost) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !post) {
    return <Navigate to={paths.team(teamSlug!)} replace />
  }

  const canEdit = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canEdit) {
    return <Navigate to={paths.post(teamSlug!, postSlug!)} replace />
  }

  const handleSubmit = (data: PostRequest) => {
    updateMutation.mutate(
      {
        slug: teamSlug!,
        postSlug: postSlug!,
        data,
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListPostsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
          toast.success(i18next.t('posts.notifications.updated'))
          navigate(paths.post(teamSlug!, postSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { slug: teamSlug!, postSlug: postSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedPost) => {
          queryClient.invalidateQueries({ queryKey: getListPostsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetPostQueryKey(teamSlug!, postSlug!) })
          navigate(paths.postEdit(teamSlug!, updatedPost.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched post data
  const initialValues: PostRequest = { ...post }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('posts.edit.title')}</h1>
      </div>

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
    </div>
  )
}
