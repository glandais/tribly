import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useAds } from '../../hooks/useAd'
import { AdCard, AdCardSkeleton } from '../../components/ad'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { Pagination } from '../../components/common/Pagination'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { paths } from '@/config/paths'
import { AdType } from '../../api/api'

const PAGE_SIZE = 20

export function AdListPage() {
  const { t } = useTranslation('ads')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [search, setSearch] = useState('')
  const [adType, setAdType] = useState<AdType | undefined>(undefined)
  const [page, setPage] = useState(0)

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const {
    data: adsResponse,
    isLoading: isLoadingAds,
    isFetching,
  } = useAds(teamSlug, {
    search: search || undefined,
    adType,
    page,
    size: PAGE_SIZE,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isMember = !!team.role
  const ads = adsResponse?.ads || []
  const totalPages = adsResponse ? Math.ceil(adsResponse.total / PAGE_SIZE) : 0

  const handleSearchChange = (value: string) => {
    setSearch(value)
    setPage(0)
  }

  const handleAdTypeChange = (value: string) => {
    setAdType(value === 'ALL' ? undefined : (value as AdType))
    setPage(0)
  }

  return (
    <TeamLayout team={team} currentTab="ads">
      <div className="py-6">
        {/* Header */}
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          {isMember && (
            <Button asChild>
              <Link to={paths.adNew(teamSlug!)}>
                <PlusIcon className="h-5 w-5 mr-2" />
                {t('list.createAd')}
              </Link>
            </Button>
          )}
        </div>

        {/* Filters */}
        <div className="flex flex-col sm:flex-row gap-4 mb-6">
          <div className="relative flex-1">
            <MagnifyingGlassIcon className="absolute left-3 top-1/2 transform -translate-y-1/2 h-5 w-5 text-gray-400" />
            <Input
              type="text"
              placeholder={t('list.search.placeholder')}
              value={search}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="pl-10"
              aria-label={t('list.search.label')}
            />
          </div>
          <Select value={adType || 'ALL'} onValueChange={handleAdTypeChange}>
            <SelectTrigger className="w-full sm:w-48">
              <SelectValue placeholder={t('list.filterByType')} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">{t('list.allTypes')}</SelectItem>
              <SelectItem value={AdType.Sale}>{t('adType.SALE')}</SelectItem>
              <SelectItem value={AdType.Rental}>{t('adType.RENTAL')}</SelectItem>
              <SelectItem value={AdType.Wanted}>{t('adType.WANTED')}</SelectItem>
            </SelectContent>
          </Select>
        </div>

        {/* Content */}
        {isLoadingAds ? (
          <div className="grid gap-4 md:grid-cols-2">
            {Array.from({ length: 4 }).map((_, i) => (
              <AdCardSkeleton key={i} />
            ))}
          </div>
        ) : ads.length === 0 ? (
          <div className="text-center py-12">
            <h2 className="text-xl font-semibold text-gray-900 mb-2">{t('list.empty.title')}</h2>
            <p className="text-gray-600">
              {search || adType ? t('list.noResults') : t('list.empty.member')}
            </p>
          </div>
        ) : (
          <>
            <div className={`grid gap-4 md:grid-cols-2 ${isFetching ? 'opacity-50' : ''}`}>
              {ads.map((ad) => (
                <AdCard key={ad.id} ad={ad} />
              ))}
            </div>

            {totalPages > 1 && (
              <div className="mt-6">
                <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
              </div>
            )}
          </>
        )}
      </div>
    </TeamLayout>
  )
}
