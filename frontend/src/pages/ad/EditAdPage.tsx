import { useParams, Navigate, useNavigate } from 'react-router-dom'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetAd,
  useUpdateAd,
  useChangeAdSlug,
  getListAdsQueryKey,
  getGetAdQueryKey,
} from '../../api/endpoints/ads/ads'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { AdEditor } from '../../components/ad/AdEditor'
import { paths } from '@/config/paths'
import { AdRequest } from '@/api/dto'

export function EditAdPage() {
  const { t } = useTranslation()
  const { teamSlug, adSlug } = useParams<{ teamSlug: string; adSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: ad, isLoading: isLoadingAd } = useGetAd(teamSlug!, adSlug!, {
    query: { enabled: !!teamSlug && !!adSlug },
  })

  const updateMutation = useUpdateAd()
  const changeSlugMutation = useChangeAdSlug()

  useCanonicalPath(team && ad ? paths.adEdit(team.slug, ad.slug) : undefined)

  if (isLoadingTeam || isLoadingAd) {
    return <LoadingPage message={t('loading')} />
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

  const handleSubmit = (data: AdRequest) => {
    updateMutation.mutate(
      {
        slug: teamSlug!,
        adSlug: adSlug!,
        data,
      },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetAdQueryKey(teamSlug!, adSlug!) })
          toast.success(i18next.t('ads.notifications.updated'))
          navigate(paths.ad(teamSlug!, adSlug!))
        },
      }
    )
  }

  const handleSlugChange = async (newSlug: string) => {
    await changeSlugMutation.mutateAsync(
      { slug: teamSlug!, adSlug: adSlug!, data: { slug: newSlug } },
      {
        onSuccess: (updatedAd) => {
          queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
          queryClient.invalidateQueries({ queryKey: getGetAdQueryKey(teamSlug!, adSlug!) })
          // Navigate to the new slug URL
          navigate(paths.adEdit(teamSlug!, updatedAd.slug), { replace: true })
        },
      }
    )
  }

  // Prepare initial values from fetched ad data
  const initialValues = {
    ...ad,
  }

  return (
    <div className="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-gray-900">{t('ads.edit.title')}</h1>
      </div>

      <AdEditor
        team={team}
        teamSlug={teamSlug!}
        initialValues={initialValues}
        onSubmit={handleSubmit}
        onCancel={() => navigate(paths.ad(teamSlug!, adSlug!))}
        isPending={updateMutation.isPending}
        submitButtonText={t('actions.save')}
        currentSlug={adSlug!}
        onSlugChange={handleSlugChange}
        canEditSlug={isAdmin}
      />
    </div>
  )
}
