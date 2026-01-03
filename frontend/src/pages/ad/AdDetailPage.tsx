import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import {
  CalendarIcon,
  PencilIcon,
  ChevronDownIcon,
  MapPinIcon,
  CurrencyEuroIcon,
} from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useAd, useUpdateAd, useDeleteAd } from '../../hooks/useAd'
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
import { AdType, RentalPeriod, Status } from '../../api/api'

const statusColors: Record<Status, string> = {
  [Status.Draft]: 'bg-gray-100 text-gray-800',
  [Status.Published]: 'bg-green-100 text-green-800',
  [Status.Cancelled]: 'bg-red-100 text-red-800',
}

const adTypeColors: Record<AdType, string> = {
  [AdType.Sale]: 'bg-indigo-100 text-indigo-800',
  [AdType.Rental]: 'bg-purple-100 text-purple-800',
  [AdType.Wanted]: 'bg-amber-100 text-amber-800',
}

export function AdDetailPage() {
  const { t } = useTranslation('ads')
  const { formatDateTime } = useFormattedDate()
  const { teamSlug, adSlug } = useParams<{ teamSlug: string; adSlug: string }>()
  const [showUnpublishConfirm, setShowUnpublishConfirm] = useState(false)
  const [showCancelConfirm, setShowCancelConfirm] = useState(false)
  const [showUncancelConfirm, setShowUncancelConfirm] = useState(false)
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ad, isLoading: isLoadingAd, error } = useAd(teamSlug, adSlug)

  const updateMutation = useUpdateAd(teamSlug, adSlug!)
  const deleteMutation = useDeleteAd(teamSlug)

  if (isLoadingTeam || isLoadingAd) {
    return <LoadingPage message={t('loading')} />
  }

  if (error || !ad) {
    return (
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <div className="text-center py-12">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">{t('detail.notFound.title')}</h1>
          <p className="text-gray-600 mb-6">{t('detail.notFound.message')}</p>
          <Link
            to={paths.ads(teamSlug!)}
            className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
          >
            {t('breadcrumb.ads')}
          </Link>
        </div>
      </div>
    )
  }

  const isAdmin = team?.role === 'ADMIN'
  // Note: Full creator check would require comparing createdById with current user ID
  // For now, backend handles authorization, frontend shows edit for all members
  const canEdit = isAdmin || !!team?.role

  const formattedDate = formatDateTime(ad.dateTime)

  const formatPrice = (price: number | undefined, adType: AdType, rentalPeriod?: RentalPeriod) => {
    if (price === undefined || price === null) {
      return t('detail.priceNegotiable')
    }
    const formattedPrice = new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'EUR',
    }).format(price)

    if (adType === AdType.Rental && rentalPeriod) {
      return `${formattedPrice} / ${t(`rentalPeriod.${rentalPeriod}`).toLowerCase()}`
    }
    return formattedPrice
  }

  const handlePublish = () => {
    updateMutation.mutate({ ...ad, status: Status.Published })
  }

  const handleUnpublish = () => {
    updateMutation.mutate({ ...ad, status: Status.Draft })
    setShowUnpublishConfirm(false)
  }

  const handleCancel = () => {
    updateMutation.mutate({ ...ad, status: Status.Cancelled })
    setShowCancelConfirm(false)
  }

  const handleUncancel = () => {
    updateMutation.mutate({ ...ad, status: Status.Published })
    setShowUncancelConfirm(false)
  }

  const handleDelete = () => {
    deleteMutation.mutate(adSlug!)
    setShowDeleteConfirm(false)
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
                  {t(`adType.${ad.adType}`)}
                </span>
                <span
                  className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${statusColors[ad.status]}`}
                >
                  {t(`status.${ad.status}`)}
                </span>
              </div>
            </div>
          </div>

          {canEdit && (
            <ButtonGroup>
              <Button asChild variant="outline">
                <Link to={paths.adEdit(teamSlug!, adSlug!)}>
                  <PencilIcon className="w-4 h-4" />
                  {t('detail.actions.edit')}
                </Link>
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="outline" className="!pl-2">
                    <ChevronDownIcon className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  {ad.status === Status.Draft && (
                    <DropdownMenuItem
                      onClick={handlePublish}
                      disabled={updateMutation.isPending}
                      className="text-green-700"
                    >
                      {updateMutation.isPending && <LoadingSpinner size="sm" />}
                      {t('detail.actions.publish')}
                    </DropdownMenuItem>
                  )}
                  {ad.status === Status.Published && (
                    <>
                      <DropdownMenuItem
                        onClick={() => setShowUnpublishConfirm(true)}
                        className="text-yellow-700"
                      >
                        {t('detail.actions.unpublish')}
                      </DropdownMenuItem>
                      <DropdownMenuItem
                        onClick={() => setShowCancelConfirm(true)}
                        className="text-yellow-700"
                      >
                        {t('detail.actions.cancel')}
                      </DropdownMenuItem>
                    </>
                  )}
                  {ad.status === Status.Cancelled && (
                    <DropdownMenuItem
                      onClick={() => setShowUncancelConfirm(true)}
                      className="text-green-700"
                    >
                      {t('detail.actions.uncancel')}
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuSeparator />
                  <DropdownMenuItem
                    onClick={() => setShowDeleteConfirm(true)}
                    variant="destructive"
                  >
                    {t('detail.actions.delete')}
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
        title={t('detail.actions.unpublish')}
        message={t('detail.confirmations.unpublish')}
        confirmText={t('detail.actions.unpublish')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showCancelConfirm}
        onClose={() => setShowCancelConfirm(false)}
        onConfirm={handleCancel}
        title={t('detail.actions.cancel')}
        message={t('detail.confirmations.cancel')}
        confirmText={t('detail.actions.cancel')}
        variant="warning"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showUncancelConfirm}
        onClose={() => setShowUncancelConfirm(false)}
        onConfirm={handleUncancel}
        title={t('detail.actions.uncancel')}
        message={t('detail.confirmations.uncancel')}
        confirmText={t('detail.actions.uncancel')}
        variant="info"
        isLoading={updateMutation.isPending}
      />
      <ConfirmDialog
        isOpen={showDeleteConfirm}
        onClose={() => setShowDeleteConfirm(false)}
        onConfirm={handleDelete}
        title={t('detail.actions.delete')}
        message={t('detail.confirmations.delete')}
        confirmText={t('detail.actions.delete')}
        variant="danger"
        isLoading={deleteMutation.isPending}
      />
    </div>
  )
}
