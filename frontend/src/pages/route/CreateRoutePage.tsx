import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { paths } from '../../config/paths'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RouteEditor } from '../../components/route/RouteEditor'
import { useCreateRoute, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { Visibility, SurfaceType, RouteRequest } from '@/api/dto'
import { defaultMedia } from '@/lib/apiUtils'

export function CreateRoutePage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { t } = useTranslation('routes')
  const queryClient = useQueryClient()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const createRouteMutation = useCreateRoute()

  const handleSubmit = async (data: RouteRequest, gpxFile?: File) => {
    // Either gpxFile or points must be provided
    if (!gpxFile && (!data.points || data.points.length < 2)) return

    const route = await createRouteMutation.mutateAsync(
      {
        slug: teamSlug!,
        data: {
          route: data,
          gpxFile,
        },
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListRoutesQueryKey(teamSlug!) })
          toast.success(i18next.t('routes:notifications.created'))
        },
      }
    )

    navigate(paths.route(teamSlug!, route.slug))
  }

  const handleCancel = () => {
    navigate(paths.routes(teamSlug!))
  }

  if (isLoadingTeam) {
    return <LoadingPage message={t('create.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  if (!canCreate) {
    return <Navigate to={paths.routes(teamSlug!)} replace />
  }

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    surfaceType: SurfaceType.ROAD,
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('create.title')}</h1>
        <p className="mt-2 text-gray-600">{t('create.subtitle')}</p>
      </div>

      <RouteEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        isCreateMode={true}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
        isPending={createRouteMutation.isPending}
        error={createRouteMutation.error}
        submitButtonText={t('create.submit')}
      />
    </div>
  )
}
