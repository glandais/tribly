import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useTeam } from '../../hooks/useTeam'
import { useAd, useUpdateAd } from '../../hooks/useAd'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { toDateTimeLocalValue } from '../../utils/dateFormat'
import { AdEditor } from '../../components/ad/AdEditor'
import type { AdFormData } from '../../components/ad/AdEditor'
import { paths } from '@/config/paths'

export function EditAdPage() {
  const { t } = useTranslation('ads')
  const { t: tCommon } = useTranslation('common')
  const { teamSlug, adSlug } = useParams<{ teamSlug: string; adSlug: string }>()
  const navigate = useNavigate()
  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ad, isLoading: isLoadingAd } = useAd(teamSlug, adSlug)

  const updateMutation = useUpdateAd(teamSlug, adSlug!)

  if (isLoadingTeam || isLoadingAd) {
    return <LoadingPage message={tCommon('loading')} />
  }

  if (!team || !ad) {
    return <Navigate to={paths.ads(teamSlug!)} replace />
  }

  // Creator or admin can edit
  const isAdmin = team.role === 'ADMIN'
  // Note: A more robust check would compare user IDs, but for now we allow any member to edit
  // if they can see the edit page (routing handles access control)
  const canEdit = isAdmin || !!team.role

  if (!canEdit) {
    return <Navigate to={paths.ad(teamSlug!, adSlug!)} replace />
  }

  const handleSubmit = async (data: AdFormData) => {
    await updateMutation.mutateAsync({
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
    })

    navigate(paths.ad(teamSlug!, adSlug!))
  }

  // Prepare initial values from fetched ad data
  const initialValues = {
    name: ad.name,
    media: ad.media,
    dateTime: toDateTimeLocalValue(ad.dateTime),
    visibility: ad.visibility,
    status: ad.status,
    adType: ad.adType,
    price: ad.price,
    rentalPeriod: ad.rentalPeriod,
    latitude: ad.latitude,
    longitude: ad.longitude,
    locationDescription: ad.locationDescription || '',
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('edit.title')}</h1>
      </div>

      <AdEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ad(teamSlug!, adSlug!))}
        isPending={updateMutation.isPending}
        error={updateMutation.error}
        submitButtonText={tCommon('actions.save')}
      />
    </div>
  )
}
