import { useState } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { PlusIcon, MagnifyingGlassIcon } from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListAds } from '../../api/endpoints/ads/ads'
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
import { AdType } from '../../api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

const PAGE_SIZE = 20

export function AdListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [search, setSearch] = useState('')
  const [adType, setAdType] = useState<AdType | undefined>(undefined)
  const [page, setPage] = useState(0)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: adsResponse,
    isLoading: isLoadingAds,
    isFetching,
  } = useListAds(
    teamSlug!,
    {
      search: search || undefined,
      adType,
      page,
      size: PAGE_SIZE,
    },
    { query: { enabled: !!teamSlug, placeholderData: keepPreviousData } }
  )

  useCanonicalPath(team ? paths.ads(team.slug) : undefined)

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
          <h2 className="text-2xl font-bold text-gray-900">{t('ads.title')}</h2>
          {isMember && (
            <Button asChild>
              <Link to={paths.adNew(teamSlug!)}>
                <PlusIcon className="h-5 w-5 mr-2" />
                {t('ads.list.createAd')}
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
              placeholder={t('ads.list.search.placeholder')}
              value={search}
              onChange={(e) => handleSearchChange(e.target.value)}
              className="pl-10"
              aria-label={t('ads.list.search.label')}
            />
          </div>
          <Select value={adType || 'ALL'} onValueChange={handleAdTypeChange}>
            <SelectTrigger className="w-full sm:w-48">
              <SelectValue placeholder={t('ads.list.filterByType')} />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">{t('ads.list.allTypes')}</SelectItem>
              <SelectItem value={AdType.SALE}>{t('ads.adType.SALE')}</SelectItem>
              <SelectItem value={AdType.RENTAL}>{t('ads.adType.RENTAL')}</SelectItem>
              <SelectItem value={AdType.WANTED}>{t('ads.adType.WANTED')}</SelectItem>
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
            <h2 className="text-xl font-semibold text-gray-900 mb-2">
              {t('ads.list.empty.title')}
            </h2>
            <p className="text-gray-600">
              {search || adType ? t('ads.list.noResults') : t('ads.list.empty.member')}
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
