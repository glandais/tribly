import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, CalendarIcon } from '@heroicons/react/24/outline'
import { useTeam } from '../../hooks/useTeam'
import { useRides } from '../../hooks/useRide'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { RideCard, RideCardSkeleton } from '../../components/ride/RideCard'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'

export function RideListPage() {
  const { t } = useTranslation('rides')
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useTeam(teamSlug)
  const { data: ridesData, isLoading: isLoadingRides } = useRides(teamSlug, {
    search: search || undefined,
    page,
    size: pageSize,
  })

  const resetPage = () => setPage(0)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: ridesData?.total ?? 0,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={t('list.notFound.title')} />
  }

  if (!team) {
    return <Navigate to="/teams" replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="rides">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">{t('list.title')}</h2>
          </div>
          {canCreate && (
            <Link
              to={`/teams/${teamSlug}/rides/new`}
              className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-4 h-4 mr-2" />
              {t('list.createRide')}
            </Link>
          )}
        </div>

        {/* Search Input */}
        <SearchInput
          id="rides-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="mb-6"
        />

        {/* Rides List */}
        {isLoadingRides ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <RideCardSkeleton key={i} />
            ))}
          </div>
        ) : ridesData?.rides && ridesData.rides.length > 0 ? (
          <>
            <div className="space-y-4">
              {ridesData.rides.map((ride) => (
                <RideCard key={ride.id} ride={ride} teamSlug={teamSlug!} />
              ))}
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
            <CalendarIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search ? t('list.noResults') : t('list.empty.title')}
            </h3>
            {!search && (
              <p className="mt-2 text-gray-500">
                {canCreate ? t('list.empty.admin') : t('list.empty.member')}
              </p>
            )}
            {canCreate && !search && (
              <Link
                to={`/teams/${teamSlug}/rides/new`}
                className="mt-4 inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700"
              >
                {t('list.empty.createAction')}
              </Link>
            )}
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
