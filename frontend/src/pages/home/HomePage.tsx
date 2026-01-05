import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { NewspaperIcon } from '@heroicons/react/24/outline'
import { useListAllPublications } from '../../api/endpoints/publications/publications'
import { PublicationType } from '@/api/dto'
import {
  PublicationCard,
  PublicationCardSkeleton,
} from '../../components/publication/PublicationCard'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

type FilterValue = 'all' | 'ride' | 'post' | 'trip'

const filterToType: Record<FilterValue, PublicationType | undefined> = {
  all: undefined,
  ride: PublicationType.RIDE,
  post: PublicationType.POST,
  trip: PublicationType.TRIP,
}

export function HomePage() {
  const { t } = useTranslation('auth')
  const { t: tTeams } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState<FilterValue>('all')
  const pageSize = 20

  const {
    data: publicationsData,
    isLoading,
    isError,
  } = useListAllPublications({
    search: search || undefined,
    page,
    size: pageSize,
    type: filterToType[filter],
  })

  const resetPage = () => setPage(0)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: publicationsData?.total ?? 0,
  })

  return (
    <HomeLayout currentTab="feed">
      {/* Publications Section */}
      <div className="mt-6 mb-6">
        <h2 className="text-2xl font-bold text-gray-900 mb-2">{t('home.feed.title')}</h2>
      </div>

      {/* Search and Filter */}
      <div className="flex flex-col sm:flex-row gap-4 mb-6">
        <SearchInput
          id="publications-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('home.feed.search.placeholder')}
          label={t('home.feed.search.label')}
          className="flex-1"
        />
        <Select
          value={filter}
          onValueChange={(value: FilterValue) => {
            setFilter(value)
            resetPage()
          }}
        >
          <SelectTrigger
            className="w-full sm:w-40"
            aria-label={tTeams('publications.list.filter.label')}
          >
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="all">{tTeams('publications.list.filter.all')}</SelectItem>
            <SelectItem value="ride">{tTeams('publications.list.filter.ride')}</SelectItem>
            <SelectItem value="post">{tTeams('publications.list.filter.post')}</SelectItem>
            <SelectItem value="trip">{tTeams('publications.list.filter.trip')}</SelectItem>
          </SelectContent>
        </Select>
      </div>

      {/* Loading State */}
      {isLoading ? (
        <div className="space-y-6">
          {[...Array(6)].map((_, i) => (
            <PublicationCardSkeleton key={i} />
          ))}
        </div>
      ) : isError ? (
        /* Error State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <NewspaperIcon className="mx-auto h-12 w-12 text-red-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">{tCommon('error.loading')}</h3>
        </div>
      ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
        /* Publications List */
        <div className="space-y-6">
          {publicationsData.publications.map((publication) => (
            <PublicationCard key={publication.id} publication={publication} showTeam={true} />
          ))}

          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={setPage}
            className="mt-8"
          />
        </div>
      ) : (
        /* Empty State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <NewspaperIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">
            {search ? t('home.feed.noResults') : t('home.feed.empty')}
          </h3>
        </div>
      )}
    </HomeLayout>
  )
}
