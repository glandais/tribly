import { useParams, Link, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { ChevronLeftIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RouteEditor } from '../../components/route/RouteEditor'
import type { RouteFormData } from '../../components/route/RouteEditor'
import { useCreateRoute, SurfaceType } from '../../hooks/useRoute'
import { Visibility } from '../../api/api'
import { defaultMedia } from '@/lib/apiUtils'

export function CreateRoutePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { t } = useTranslation('routes')

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const createRoute = useCreateRoute(teamSlug!)

  const handleSubmit = async (data: RouteFormData, gpxFile?: File) => {
    // Either gpxFile or points must be provided
    if (!gpxFile && (!data.points || data.points.length < 2)) return

    const route = await createRoute.mutateAsync({
      route: {
        name: data.name,
        media: data.media,
        surfaceType: data.surfaceType,
        visibility: data.visibility,
        points: data.points,
      },
      gpxFile,
    })

    navigate(`/teams/${teamSlug}/routes/${route.slug}`)
  }

  const handleCancel = () => {
    navigate(`/teams/${teamSlug}/routes`)
  }

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={`/teams/${teamSlug}/routes`} replace />
  }

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    surfaceType: SurfaceType.Road,
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <Link
          to={`/teams/${teamSlug}/routes`}
          className="inline-flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeftIcon className="w-4 h-4 mr-1" />
          {t('create.backToList')}
        </Link>
        <h1 className="mt-4 text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-2 text-gray-600">{t('create.subtitle')}</p>
      </div>

      <RouteEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        isCreateMode={true}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isPending={createRoute.isPending}
        error={createRoute.error}
        submitButtonText={t('create.submit')}
      />
    </div>
  )
}
