import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, UserGroupIcon } from '@heroicons/react/24/outline'
import { useTeams } from '../../hooks/useTeam'
import { useAuth } from '../../hooks/useAuth'
import { usePagination } from '../../hooks/usePagination'
import { TeamCard, TeamCardSkeleton } from '../../components/team/TeamCard'
import { Pagination } from '../../components/common/Pagination'
import { SearchInput } from '../../components/common/SearchInput'

export function TeamListPage() {
  const { t } = useTranslation('teams')
  const { t: tErrors } = useTranslation('errors')
  const [search, setSearch] = useState('')
  const { isAuthenticated } = useAuth()

  const [page, setPage] = useState(0)
  const pageSize = 12

  const {
    data: teamsData,
    isLoading,
    error,
  } = useTeams({
    search,
    page,
    size: pageSize,
  })

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: teamsData?.total ?? 0,
  })

  const teams = teamsData?.teams

  const resetPage = () => setPage(0)

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{t('list.title')}</h1>
          <p className="mt-1 text-gray-700">{t('list.subtitle')}</p>
        </div>
        {isAuthenticated && (
          <Link
            to="/teams/new"
            className="mt-4 sm:mt-0 inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-indigo-500"
          >
            <PlusIcon className="w-5 h-5 mr-2 -ml-1" aria-hidden="true" />
            {t('list.createTeam')}
          </Link>
        )}
      </div>

      <SearchInput
        id="team-search"
        value={search}
        onChange={(value) => {
          setSearch(value)
          resetPage()
        }}
        placeholder={t('list.search.placeholder')}
        label={t('list.search.label')}
        className="mb-6"
      />

      {error && (
        <div className="mb-6 p-4 bg-red-50 border border-red-200 rounded-lg">
          <p className="text-red-700">
            {error instanceof Error ? error.message : tErrors('api.failedToLoad')}
          </p>
        </div>
      )}

      {isLoading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          <TeamCardSkeleton count={6} />
        </div>
      ) : teams && teams.length > 0 ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {teams.map((team) => (
              <TeamCard key={team.id} team={team} showRole={true} />
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
        <div className="text-center py-12">
          <UserGroupIcon className="mx-auto h-12 w-12 text-gray-400" aria-hidden="true" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">{t('list.empty.title')}</h3>
          <p className="mt-1 text-sm text-gray-700">{t('list.empty.publicTeams')}</p>
          {isAuthenticated && (
            <div className="mt-6">
              <Link
                to="/teams/new"
                className="inline-flex items-center px-4 py-2 border border-transparent shadow-xs text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-indigo-500"
              >
                <PlusIcon className="w-5 h-5 mr-2 -ml-1" aria-hidden="true" />
                {t('list.empty.createAction')}
              </Link>
            </div>
          )}
        </div>
      )}
    </div>
  )
}
