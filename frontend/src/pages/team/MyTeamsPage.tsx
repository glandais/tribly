import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, UserGroupIcon } from '@heroicons/react/24/outline'
import { useMyTeams } from '../../hooks/useTeam'
import { TeamCard, TeamCardSkeleton } from '../../components/team/TeamCard'

export function MyTeamsPage() {
  const { t } = useTranslation('teams')
  const { t: tErrors } = useTranslation('errors')

  const { data: teams, isLoading, error } = useMyTeams()

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">{t('myTeams.title')}</h1>
          <p className="mt-1 text-gray-600">{t('myTeams.subtitle')}</p>
        </div>
        <Link
          to="/teams/new"
          className="mt-4 sm:mt-0 inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-hidden focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
        >
          <PlusIcon className="w-5 h-5 mr-2 -ml-1" />
          {t('myTeams.createTeam')}
        </Link>
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
      ) : teams && teams.teams.length > 0 ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {teams.teams.map((team) => (
            <TeamCard key={team.id} team={team} showRole={true} />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <UserGroupIcon className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900">{t('myTeams.empty.title')}</h3>
          <p className="mt-1 text-sm text-gray-500">{t('myTeams.empty.description')}</p>
          <div className="mt-6">
            <Link
              to="/teams/new"
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-xs text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
            >
              <PlusIcon className="w-5 h-5 mr-2 -ml-1" />
              {t('myTeams.empty.createAction')}
            </Link>
          </div>
        </div>
      )}
    </div>
  )
}
