import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import { NewspaperIcon, UsersIcon } from '@heroicons/react/24/outline'
import { useAllPublications } from '../../hooks/useAllPublications'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { PostCard, PostCardSkeleton } from '../../components/post/PostCard'
import { TripCard, TripCardSkeleton } from '../../components/trip/TripCard'
import type { RideDto, PostDto, TripDto } from '../../api/api'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'

export function HomePage() {
  const { t } = useTranslation('auth')
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const pageSize = 20

  const {
    data: publicationsData,
    isLoading,
    isError,
  } = useAllPublications({
    search: search || undefined,
    page,
    size: pageSize,
  })

  const resetPage = () => setPage(0)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: publicationsData?.total ?? 0,
  })

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Header Section */}
      <div className="mb-8">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">{t('home.title')}</h1>
        <p className="text-lg text-gray-600">{t('home.subtitle')}</p>
      </div>

      {/* Publications Section */}
      <div className="mb-6">
        <h2 className="text-2xl font-bold text-gray-900 mb-2">{t('home.feed.title')}</h2>
        <p className="text-gray-600">{t('home.feed.subtitle')}</p>
      </div>

      {/* Search Input */}
      <SearchInput
        id="publications-search"
        value={search}
        onChange={(value) => {
          setSearch(value)
          resetPage()
        }}
        placeholder={t('home.feed.search.placeholder')}
        label={t('home.feed.search.label')}
        className="mb-6"
      />

      {/* Loading State */}
      {isLoading ? (
        <div className="space-y-6">
          {[...Array(6)].map((_, i) =>
            // Alternate between ride, post, and trip skeletons for visual variety
            i % 3 === 0 ? (
              <RideCardSkeleton key={i} />
            ) : i % 3 === 1 ? (
              <PostCardSkeleton key={i} />
            ) : (
              <TripCardSkeleton key={i} />
            )
          )}
        </div>
      ) : isError ? (
        /* Error State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <NewspaperIcon className="mx-auto h-12 w-12 text-red-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">{t('home.feed.error')}</h3>
        </div>
      ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
        /* Publications List */
        <div className="space-y-6">
          {publicationsData.publications.map((publication) => (
            <div key={publication.id} className="space-y-1">
              {/* Team Name Label */}
              <div className="text-xs text-gray-500 flex items-center gap-1">
                <UsersIcon className="h-4 w-4" />
                {publication.team?.name || 'Unknown Team'}
              </div>

              {/* Publication Card */}
              {publication.type === 'RIDE' ? (
                <RideCard
                  ride={publication as RideDto}
                  teamSlug={publication.team?.slug || ''}
                  showTypeBadge={true}
                />
              ) : publication.type === 'TRIP' ? (
                <TripCard
                  trip={publication as TripDto}
                  teamSlug={publication.team?.slug || ''}
                  showTypeBadge={true}
                />
              ) : (
                <PostCard
                  post={publication as PostDto}
                  teamSlug={publication.team?.slug || ''}
                  showTypeBadge={true}
                />
              )}
            </div>
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
    </div>
  )
}
