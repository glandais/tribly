import { useState } from 'react'
import { Link, useParams, Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { PlusIcon, NewspaperIcon, ChevronDownIcon } from '@heroicons/react/24/outline'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListPublications } from '../../api/endpoints/publications/publications'
import { PublicationType } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import {
  PublicationCard,
  PublicationCardSkeleton,
} from '../../components/publication/PublicationCard'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { SearchInput } from '../../components/common/SearchInput'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { Button } from '@/components/ui/button'
import { ButtonGroup } from '@/components/ui/button-group'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { paths } from '@/config/paths'

type FilterValue = 'all' | 'ride' | 'post' | 'trip'

const filterToType: Record<FilterValue, PublicationType | undefined> = {
  all: undefined,
  ride: PublicationType.RIDE,
  post: PublicationType.POST,
  trip: PublicationType.TRIP,
}

export function PublicationListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState<FilterValue>('all')
  const pageSize = 20

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: publicationsData, isLoading: isLoadingPublications } = useListPublications(
    teamSlug!,
    {
      search: search || undefined,
      page,
      size: pageSize,
      type: filterToType[filter],
    },
    { query: { enabled: !!teamSlug } }
  )

  const resetPage = () => setPage(0)

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: publicationsData?.total ?? 0,
  })

  if (isLoadingTeam) {
    return <LoadingPage message={t('teams.publications.list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="publications">
      <div className="py-6">
        <div className="flex items-center justify-between mb-6">
          <div>
            <h2 className="text-2xl font-bold text-gray-900">
              {t('teams.publications.list.title')}
            </h2>
          </div>
          {canCreate && (
            <ButtonGroup>
              <Button asChild className="bg-indigo-600 hover:bg-indigo-700">
                <Link to={paths.rideNew(teamSlug!)}>
                  <PlusIcon className="w-4 h-4" />
                  {t('rides.create.title')}
                </Link>
              </Button>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button className="!pl-2 bg-indigo-600 hover:bg-indigo-700">
                    <ChevronDownIcon className="w-4 h-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuItem asChild>
                    <Link to={paths.rideNew(teamSlug!)}>{t('rides.create.title')}</Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to={paths.postNew(teamSlug!)}>{t('posts.create.title')}</Link>
                  </DropdownMenuItem>
                  {team.enableTrips && (
                    <DropdownMenuItem asChild>
                      <Link to={paths.tripNew(teamSlug!)}>{t('trips.create.title')}</Link>
                    </DropdownMenuItem>
                  )}
                  <DropdownMenuItem asChild>
                    <Link to={paths.routeNew(teamSlug!)}>{t('routes.create.title')}</Link>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </ButtonGroup>
          )}
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
            placeholder={t('teams.publications.list.search.placeholder')}
            label={t('teams.publications.list.search.label')}
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
              aria-label={t('teams.publications.list.filter.label')}
            >
              <SelectValue />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">{t('teams.publications.list.filter.all')}</SelectItem>
              <SelectItem value="ride">{t('teams.publications.list.filter.ride')}</SelectItem>
              <SelectItem value="post">{t('teams.publications.list.filter.post')}</SelectItem>
              {team?.enableTrips && (
                <SelectItem value="trip">{t('teams.publications.list.filter.trip')}</SelectItem>
              )}
            </SelectContent>
          </Select>
        </div>

        {/* Publications List */}
        {isLoadingPublications ? (
          <div className="space-y-4">
            {[...Array(3)].map((_, i) => (
              <PublicationCardSkeleton key={i} />
            ))}
          </div>
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          <>
            <div className="space-y-4">
              {publicationsData.publications.map((publication) => (
                <PublicationCard key={publication.id} publication={publication} showTeam={false} />
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
            <NewspaperIcon className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-4 text-lg font-medium text-gray-900">
              {search ? t('noResults') : t('teams.publications.list.empty')}
            </h3>
            {!search && (
              <p className="mt-2 text-gray-500">{t('teams.publications.list.emptyDescription')}</p>
            )}
          </div>
        )}
      </div>
    </TeamLayout>
  )
}
