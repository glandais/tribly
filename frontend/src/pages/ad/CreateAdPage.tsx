import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useCreateAd, getListAdsQueryKey } from '../../api/endpoints/ads/ads'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { AdEditor } from '../../components/ad/AdEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'
import { AdRequest, AdType, Status, Visibility } from '../../api/dto'

export function CreateAdPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })

  const createMutation = useCreateAd()

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team || !teamSlug) {
    return <Navigate to={paths.teams()} replace />
  }

  // Any member can create ads
  const canCreate = !!team.role

  if (!canCreate) {
    return <Navigate to={paths.ads(teamSlug!)} replace />
  }

  // Prepare initial values for create mode
  const initialValues = {
    name: '',
    media: defaultMedia(),
    dateTime: new Date().toISOString(),
    visibility: team.visibility === Visibility.TEAM ? Visibility.TEAM : Visibility.PUBLIC,
    status: Status.DRAFT,
    adType: AdType.SALE,
    price: undefined,
    rentalPeriod: undefined,
    latitude: undefined,
    longitude: undefined,
    locationDescription: '',
  }

  const handleSubmit = (data: AdRequest) => {
    createMutation.mutate(
      {
        slug: teamSlug!,
        data,
      },
      {
        onSuccess: (ad) => {
          queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
          toast.success(i18next.t('ads.notifications.created'))
          navigate(paths.ad(teamSlug!, ad.slug))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('ads.create.title')}</h1>
      </div>

      <AdEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ads(teamSlug!))}
        isPending={createMutation.isPending}
        submitButtonText={t('ads.create.submit')}
      />
    </div>
  )
}
