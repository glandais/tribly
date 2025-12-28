import { Link, useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useCreatePost, Visibility, Status } from '../../hooks/usePost'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { PostEditor } from '../../components/post/PostEditor'
import type { PostFormData } from '../../components/post/PostEditor'

export function CreatePostPage() {
  const { t } = useTranslation('posts')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreatePost(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/posts`} replace />
  }

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: { markdown: '', assets: [] },
    dateTime: toDateTimeLocalValue(new Date()),
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
    status: Status.Draft,
    publishAt: undefined,
  }

  const handleSubmit = (data: PostFormData) => {
    createMutation.mutate(
      {
        name: data.name,
        media: data.media,
        dateTime: data.dateTime,
        status: data.status,
        visibility: data.visibility,
        publishAt: data.publishAt,
      },
      {
        onSuccess: () => {
          navigate(`/teams/${teamSlug}/posts`)
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/posts`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('breadcrumb.posts')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
      </div>

      <PostEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(`/teams/${teamSlug}/posts`)}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.submit')}
      />
    </div>
  )
}
