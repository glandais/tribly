import { useTranslation } from 'react-i18next'
import { NewspaperIcon, UsersIcon } from '@heroicons/react/24/outline'
import { useAllPublications } from '../../hooks/useAllPublications'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { PostCard, PostCardSkeleton } from '../../components/post/PostCard'
import type { RideDto, PostDto } from '../../api/api'

export function HomePage() {
  const { t } = useTranslation('auth')
  const { data: publicationsData, isLoading, isError } = useAllPublications()

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

      {/* Loading State */}
      {isLoading ? (
        <div className="space-y-6">
          {[...Array(6)].map((_, i) =>
            // Alternate between ride and post skeletons for visual variety
            i % 2 === 0 ? <RideCardSkeleton key={i} /> : <PostCardSkeleton key={i} />
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
              ) : (
                <PostCard
                  post={publication as PostDto}
                  teamSlug={publication.team?.slug || ''}
                  showTypeBadge={true}
                />
              )}
            </div>
          ))}
        </div>
      ) : (
        /* Empty State */
        <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
          <NewspaperIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-4 text-lg font-medium text-gray-900">{t('home.feed.empty')}</h3>
        </div>
      )}
    </div>
  )
}
