import { useState } from 'react'
import { Link, useParams, useNavigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { useQueryClient } from '@tanstack/react-query'
import { toast } from 'sonner'
import i18next from 'i18next'
import {
  CalendarIcon,
  PencilIcon,
  ChevronDownIcon,
  MapPinIcon,
  CurrencyEuroIcon,
} from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useGetAd,
  useUpdateAd,
  useDeleteAd,
  getListAdsQueryKey,
  getGetAdQueryKey,
} from '../../api/endpoints/ads/ads'
import { LoadingPage, LoadingSpinner } from '../../components/common/LoadingSpinner'
import { ConfirmDialog } from '../../components/common/ConfirmDialog'
import { MediaDisplay } from '../../components/common/MediaDisplay'
import { EntityLogo } from '../../components/common/EntityLogo'
import { useFormattedDate } from '../../utils/dateFormat'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { paths } from '@/config/paths'
import { AdType, RentalPeriod, Status } from '../../api/dto'

const statusColors: Record<Status, string> = {
  [Status.DRAFT]: 'bg-gray-100 text-gray-800',
  [Status.PUBLISHED]: 'bg-green-100 text-green-800',
  [Status.CANCELLED]: 'bg-red-100 text-red-800',
}

const adTypeColors: Record<AdType, string> = {
  [AdType.SALE]: 'bg-indigo-100 text-indigo-800',
  [AdType.RENTAL]: 'bg-purple-100 text-purple-800',
  [AdType.WANTED]: 'bg-amber-100 text-amber-800',
}

export function AdDetailPage() {
  const { t } = useTranslation()
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, adSlug } = useParams<{ teamSlug: string; adSlug: string }>()
  const navigate = useNavigate()
  const queryClient = useQueryClient()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: ad,
    isLoading: isLoadingAd,
    error,
  } = useGetAd(teamSlug!, adSlug!, { query: { enabled: !!teamSlug && !!adSlug } })

  const updateMutation = useUpdateAd()
  const deleteMutation = useDeleteAd()

  if (isLoadingTeam || isLoadingAd) {
    return <LoadingPage message={t('loading')} />
  }

  if (error || !ad) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">
            {t('ads.detail.notFound.title')}
          </h1>
          <p className="text-gray-600 mb-6">{t('ads.detail.notFound.message')}</p>
          <Link
            to={paths.ads(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('ads.title')}
          </Link>
        </div>
      </div>
    )
  }

  const isAdmin = team?.role === 'ADMIN'
  // Note: Full creator check would require comparing createdById with current user ID
  // For now, backend handles authorization, frontend shows edit for all members
  const canEdit = isAdmin || !!team?.role

  const formattedDate = formatDateTime(ad.createdAt)

  const formatPrice = (price: number | undefined, adType: AdType, rentalPeriod?: RentalPeriod) => {
    if (price === undefined || price === null) {
      return t('ads.detail.priceNegotiable')
    }
    const formattedPrice = new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(price)

    if (adType === AdType.RENTAL && rentalPeriod) {
      return `${formattedPrice} / ${t(`ads.rentalPeriod.${rentalPeriod satisfies 'DAY' | 'WEEK' | 'MONTH'}`).toLowerCase()}`
    }
    return formattedPrice
  }

  const invalidateAds = () => {
    queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
    queryClient.invalidateQueries({ queryKey: getGetAdQueryKey(teamSlug!, adSlug!) })
  }

  const handlePublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, adSlug: adSlug!, data: { ...ad, status: Status.PUBLISHED } },
      {
        onSuccess: () => {
          invalidateAds()
          toast.success(i18next.t('ads.notifications.updated'))
        },
      }
    )
  }

  const handleUnpublish = () => {
    updateMutation.mutate(
      { slug: teamSlug!, adSlug: adSlug!, data: { ...ad, status: Status.DRAFT } },
      {
        onSuccess: () => {
          invalidateAds()
          toast.success(i18next.t('ads.notifications.updated'))
          setShowUnpublishConfirm(false)
        },
      }
    )
  }

  const handleDelete = () => {
    deleteMutation.mutate(
      { slug: teamSlug!, adSlug: adSlug! },
      {
        onSuccess: () => {
          queryClient.invalidateQueries({ queryKey: getListAdsQueryKey(teamSlug!) })
          toast.success(i18next.t('ads.notifications.deleted'))
          setShowDeleteConfirm(false)
          navigate(paths.ads(teamSlug!))
        },
      }
    )
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header */}
      <div className="bg-white rounded-lg shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3 min-w-0">
            <EntityLogo logo={ad.media.assets.logo} alt={ad.name} size="lg" />
            <div className="min-w-0">
              <h1 className="text-2xl font-bold text-gray-900 truncate">{ad.name}</h1>
              <div className="flex flex-wrap items-center gap-2 mt-1">
                <span
                  className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${adTypeColors[ad.adType]}`}
                >
                  {t(`ads.adType.${ad.adType satisfies 'SALE' | 'RENTAL' | 'WANTED'}`)}
                </span>
                <span
                  className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ad.status]}`}
                >
                  {t(`status.${ad.status satisfies 'DRAFT' | 'PUBLISHED' | 'CANCELLED'}`)}
                </span>
              </div>
            </div>
          </div>

          {canEdit && (
            <ButtonGroup>
              <Button asChild variant="outline">
                <Link to={paths.adEdit(teamSlug!, adSlug!)}>
                  <PencilIcon className="w-4 h-4" />
                  {t('actions.edit')}
                </Link>
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" className="!pl-2">
                    <ChevronDownIcon className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  {ad.status === Status.DRAFT && (
                    <DropdownMenuItem
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      className="text-green-700"
                    >
                      {updateMutation.isPending && <LoadingSpinner size="sm" />}
                      {t('actions.publish')}
                    </DropdownMenuItem>
                  )}
                  {ad.status === Status.PUBLISHED && (
                    <>
                      <DropdownMenuItem
                        onClick={() => setShowUnpublishConfirm(true)}
                        className="text-yellow-700"
                      >
                        {t('actions.unpublish')}
                      </DropdownMenuItem>
                    </>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={() => setShowDeleteConfirm(true)}
                    variant="destructive"
                  >
                    {t('actions.delete')}
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </ButtonGroup>
          )}
        </div>

        {/* Price */}
        <div className="mt-6 p-4 bg-gray-50 rounded-lg">
          <div className="flex items-center gap-2 text-2xl font-bold text-gray-900">
            <CurrencyEuroIcon className="w-6 h-6" />
            {formatPrice(ad.price, ad.adType, ad.rentalPeriod)}
          </div>
        </div>

        {/* Description */}
        <div className="mt-4">
          <MediaDisplay media={ad.media} className="text-gray-600" />
        </div>

        {/* Meta info */}
        <div className="mt-4 flex flex-wrap items-center gap-4 text-sm text-gray-500">
          <span className="flex items-center">
            <CalendarIcon className="w-4 h-4 mr-1" />
            {formattedDate}
          </span>
          {ad.locationDescription && (
            <span className="flex items-center">
              <MapPinIcon className="w-4 h-4 mr-1" />
              {ad.locationDescription}
            </span>
          )}
        </div>
      </div>

      {/* Confirmation Dialogs */}
      <ConfirmDialog
        isOpen={showUnpublishConfirm}
        onClose={() => setShowUnpublishConfirm(false)}
        onConfirm={handleUnpublish}
        title={t('actions.unpublish')}
        message={t('ads.detail.confirmations.unpublish')}
        confirmText={t('actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('actions.delete')}
        message={t('ads.detail.confirmations.delete')}
        confirmText={t('actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
