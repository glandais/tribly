import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { usePost, useUpdatePost } from '../../hooks/usePost'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { PostEditor } from '../../components/post/PostEditor'
import type { PostFormData } from '../../components/post/PostEditor'
import { paths } from '@/config/paths'

export function EditPostPage() {
  const { t } = useTranslation('posts')
  const { teamSlug, postSlug } = useParams<{ teamSlug: string; postSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: post, isLoading: isLoadingPost } = usePost(teamSlug, postSlug)

  const updateMutation = useUpdatePost(teamSlug, postSlug!)

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

  const handleSubmit = async (data: PostFormData) => {
    // Update post details
    await updateMutation.mutateAsync({
      name: data.name,
      media: data.media,
      dateTime: data.dateTime,
      status: data.status,
      visibility: data.visibility,
      publishAt: data.publishAt,
    })

    navigate(paths.post(teamSlug!, postSlug!))
  }

  // Prepare initial values from fetched post data
  const initialValues = {
    name: post.name,
    media: post.media,
    dateTime: toDateTimeLocalValue(post.dateTime),
    visibility: post.visibility,
    status: post.status,
    publishAt: post.publishAt ? toDateTimeLocalValue(post.publishAt) : undefined,
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
      </div>

      <PostEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.post(teamSlug!, postSlug!))}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={t('edit.submit')}
      />
    </div>
  )
}
