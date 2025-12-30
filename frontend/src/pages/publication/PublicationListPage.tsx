import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, NewspaperIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { usePublications } from '../../hooks/usePublications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { PostCard, PostCardSkeleton } from '../../components/post/PostCard'
import { TripCard, TripCardSkeleton } from '../../components/trip/TripCard'
import { TeamLayout } from '../../components/team/TeamLayout'
import type { RideDto, PostDto, TripDto } from '../../api/api'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'

export function PublicationListPage() {
  const { t } = useTranslation('teams')
  const { t: tRides } = useTranslation('rides')
  const { t: tPosts } = useTranslation('posts')
  const { t: tRoutes } = useTranslation('routes')
  const { t: tTrips } = useTranslation('trips')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: publicationsData, isLoading: isLoadingPublications } = usePublications(teamSlug, {
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

  if (isLoadingTeam) {
    return <LoadingPage message={t('publications.list.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="publications">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('publications.list.title')}</h2>
          </div>
          {canCreate && (
            <div className="flex items-center gap-2">
              <Link
                to={`/teams/${teamSlug}/rides/new`}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                <PlusIcon className="w-4 h-4 mr-2" />
                {tRides('list.createRide')}
              </Link>
              <Link
                to={`/teams/${teamSlug}/posts/new`}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                <PlusIcon className="w-4 h-4 mr-2" />
                {tPosts('list.createPost')}
              </Link>
              <Link
                to={`/teams/${teamSlug}/trips/new`}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                <PlusIcon className="w-4 h-4 mr-2" />
                {tTrips('list.createTrip')}
              </Link>
              <Link
                to={`/teams/${teamSlug}/routes/new`}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                <PlusIcon className="w-4 h-4 mr-2" />
                {tRoutes('list.createRoute')}
              </Link>
            </div>
          )}
        </div>

        {/* Search Input */}
        <SearchInput
          id="publications-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('publications.list.search.placeholder')}
          label={t('publications.list.search.label')}
          className="mb-6"
        />

        {/* Publications List */}
        {isLoadingPublications ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) =>
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
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          <>
            <div className="space-y-4">
              {publicationsData.publications.map((publication) => {
                // Discriminated union type narrowing based on 'type' field
                if (publication.type === 'RIDE') {
                  return (
                    <RideCard
                      key={publication.id}
                      ride={publication as RideDto}
                      teamSlug={teamSlug!}
                      showTypeBadge={true}
                    />
                  )
                }
                if (publication.type === 'TRIP') {
                  return (
                    <TripCard
                      key={publication.id}
                      trip={publication as TripDto}
                      teamSlug={teamSlug!}
                      showTypeBadge={true}
                    />
                  )
                }
                return (
                  <PostCard
                    key={publication.id}
                    post={publication as PostDto}
                    teamSlug={teamSlug!}
                    showTypeBadge={true}
                  />
                )
              })}
            </div>

            <Pagination
              currentPage={page}
              totalPages={totalPages}
              onPageChange={setPage}
              className="mt-8"
            />
          </>
        ) : (
          <div className="text-center py-12 bg-white rounded-lg shadow-sm border border-gray-200">
            <NewspaperIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search ? t('publications.list.noResults') : t('publications.list.empty')}
            </h3>
            {!search && (
              <p className="mt-2 text-gray-500">{t('publications.list.emptyDescription')}</p>
            )}
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
