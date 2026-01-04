import { useState } from 'react'
import { Link } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { PlusIcon, UserGroupIcon } from '@heroicons/react/24/outline'
import { useListTeams } from '@/api/endpoints/teams/teams'
import { MinRole } from '@/api/dto'
import { useAuth } from '../../hooks/useAuth'
import { usePagination } from '../../hooks/usePagination'
import { TeamCard, TeamCardSkeleton } from '../../components/team/TeamCard'
import { Pagination } from '../../components/common/Pagination'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'

type FilterValue = 'all' | 'member' | 'organizer' | 'admin'

const filterToMinRole: Record<FilterValue, MinRole | undefined> = {
  all: undefined,
  member: MinRole.MEMBER,
  organizer: MinRole.ORGANIZER,
  admin: MinRole.ADMIN,
}

export function TeamListPage() {
  const { t } = useTranslation('teams')
  const { t: tCommon } = useTranslation('common')
  const { t: tErrors } = useTranslation('errors')
  const [search, setSearch] = useState('')
  const { isAuthenticated } = useAuth()

  const [page, setPage] = useState(0)
  const pageSize = 12
  const [filter, setFilter] = useState<FilterValue>(isAuthenticated ? 'member' : 'all')

  const {
    data: teamsData,
    isLoading,
    error,
  } = useListTeams({
    search,
    page,
    size: pageSize,
    minRole: filterToMinRole[filter],
  })

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: teamsData?.total ?? 0,
  })

  const teams = teamsData?.teams

  const resetPage = () => setPage(0)

  return (
    <HomeLayout currentTab="teams">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between mt-6 mb-8">
        <div>
          <h2 className="text-2xl font-bold text-gray-900">{tCommon('teams')}</h2>
          <p className="mt-1 text-gray-700">{t('list.subtitle')}</p>
        </div>
        {isAuthenticated && (
          <Link
            to={paths.teamsNew()}
            className="mt-4 sm:mt-0 inline-flex items-center justify-center px-4 py-2 border border-transparent rounded-md shadow-xs text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-indigo-500"
          >
            <PlusIcon className="w-5 h-5 mr-2 -ml-1" aria-hidden="true" />
            {t('create.title')}
          </Link>
        )}
      </div>

      <div className="flex flex-col sm:flex-row gap-4 mb-6">
        <SearchInput
          id="team-search"
          value={search}
          onChange={(value) => {
            setSearch(value)
            resetPage()
          }}
          placeholder={t('list.search.placeholder')}
          label={t('list.search.label')}
          className="flex-1"
        />
        {isAuthenticated && (
          <Select
            value={filter}
            onValueChange={(value: FilterValue) => {
              setFilter(value)
              resetPage()
            }}
          >
            <SelectTrigger className="w-full sm:w-40" aria-label={t('list.filter.label')}>
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t('list.filter.all')}</SelectItem>
              <SelectItem value="member">{tCommon('roles.MEMBER')}</SelectItem>
              <SelectItem value="organizer">{tCommon('roles.ORGANIZER')}</SelectItem>
              <SelectItem value="admin">{tCommon('roles.ADMIN')}</SelectItem>
            </SelectContent>
          </Select>
        )}
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
                to={paths.teamsNew()}
                className="inline-flex items-center px-4 py-2 border border-transparent shadow-xs text-sm font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 focus-visible:ring-indigo-500"
              >
                <PlusIcon className="w-5 h-5 mr-2 -ml-1" aria-hidden="true" />
                {t('create.title')}
              </Link>
            </div>
          )}
        </div>
      )}
    </HomeLayout>
  )
}
