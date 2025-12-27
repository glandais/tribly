import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, MagnifyingGlassIcon, UserGroupIcon } from '@heroicons/react/24/outline'
import { useTeams } from '../../hooks/useTeam'
import { useAuth } from '../../hooks/useAuth'
import { TeamCard, TeamCardSkeleton } from '../../components/team/TeamCard'

export function TeamListPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')
  const [search, setSearch] = useState('')
  const [page, setPage] = useState(0)
  const { isAuthenticated } = useAuth()

  const { data: teamsData, isLoading, error } = useTeams({ search, page, size: 12 })

  const teams = teamsData?.teams
  const total = teamsData?.total ?? 0
  const pageSize = 12
  const totalPages = Math.ceil(total / pageSize)

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

      <div className="mb-6">
        <label htmlFor="team-search" className="sr-only">
          {t('list.search.label')}
        </label>
        <div className="relative">
          <input
            id="team-search"
            type="search"
            placeholder={t('list.search.placeholder')}
            value={search}
            onChange={(e) => {
              setSearch(e.target.value)
              setPage(0)
            }}
            className="w-full sm:max-w-md px-4 py-2 pl-10 border border-gray-300 rounded-lg text-gray-900 placeholder-gray-500 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-indigo-500 focus-visible:border-indigo-500"
            aria-label={t('list.search.label')}
          />
          <MagnifyingGlassIcon
            className="absolute left-3 top-2.5 h-5 w-5 text-gray-400 pointer-events-none"
            aria-hidden="true"
          />
        </div>
      </div>

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

          {totalPages > 1 && (
            <div className="mt-8 flex items-center justify-center gap-2">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {tCommon('buttons.previous')}
              </button>
              <span className="text-sm text-gray-700">
                {tCommon('pagination.page', { current: page + 1, total: totalPages })}
              </span>
              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-4 py-2 border border-gray-300 rounded-md text-sm font-medium text-gray-700 bg-white hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {tCommon('buttons.next')}
              </button>
            </div>
          )}
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
