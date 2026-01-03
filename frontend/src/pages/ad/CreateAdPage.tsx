import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { useCreateAd } from '../../hooks/useAd'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { AdEditor } from '../../components/ad/AdEditor'
import type { AdFormData } from '../../components/ad/AdEditor'
import { defaultMedia } from '@/lib/apiUtils'
import { paths } from '@/config/paths'
import { AdType, Status, Visibility } from '../../api/api'

export function CreateAdPage() {
  const { t } = useTranslation('ads')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)

  const createMutation = useCreateAd(teamSlug)

  if (isLoadingTeam) {
    return <LoadingPage message={tCommon('loading')} />
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
    dateTime: toDateTimeLocalValue(new Date()),
    visibility: team.visibility === Visibility.Team ? Visibility.Team : Visibility.Public,
    status: Status.Draft,
    adType: AdType.Sale,
    price: undefined,
    rentalPeriod: undefined,
    latitude: undefined,
    longitude: undefined,
    locationDescription: '',
  }

  const handleSubmit = (data: AdFormData) => {
    createMutation.mutate(
      {
        name: data.name,
        media: data.media,
        dateTime: data.dateTime,
        status: data.status,
        visibility: data.visibility,
        adType: data.adType,
        price: data.price,
        rentalPeriod: data.rentalPeriod,
        latitude: data.latitude,
        longitude: data.longitude,
        locationDescription: data.locationDescription,
      },
      {
        onSuccess: () => {
          navigate(paths.ads(teamSlug!))
        },
      }
    )
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('create.title')}</h1>
      </div>

      <AdEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ads(teamSlug!))}
        isPending={createMutation.isPending}
        error={createMutation.error}
        submitButtonText={t('create.submit')}
      />
    </div>
  )
}
