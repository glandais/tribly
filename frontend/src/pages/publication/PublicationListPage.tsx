import { useCallback } from 'react'
import { OutOfRangeState } from '@/components/common/OutOfRangeState'
import { Navigate, useParams } from 'react-router-dom'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation } from 'react-i18next'
import { IconPlus, IconNews, IconChevronDown, IconSearchOff } from '@tabler/icons-react'
import {
  Button,
  Container,
  Menu,
  Select,
  Stack,
  Group,
  Title,
  Box,
  SimpleGrid,
} from '@mantine/core'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import {
  PublicationCard,
  PublicationCardActions,
  PublicationCardSkeleton,
} from '../../components/card'
import { TeamLayout } from '../../components/team/TeamLayout'
import { EmptyState } from '../../components/common/EmptyState'
import { Pagination } from '../../components/common/Pagination'
import { ResultCount } from '../../components/common/ResultCount'
import { PublicationScopeControl } from '../../components/home/PublicationScopeControl'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import {
  type PublicationFilterValue,
  publicationFilterToType,
} from '../../hooks/filters/publicationFilters'
import { TagFilter } from '../../components/tag'
import { SearchInput } from '../../components/common/SearchInput'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { QueryStateBoundary } from '../../components/common/QueryStateBoundary'
import { apiErrorStatus } from '@/lib/apiError'
import type { TeamDetailDto } from '@/api/dto'
import { type PublicationListKind, usePublicationListData } from './publicationListData'

interface PublicationListPageProps {
  /**
   * Set on the « Sorties » and « Voyages » tabs (`teamRides`, `teamTrips`): the same feed, narrowed
   * to one kind by the route instead of `?type=`, so the type select gives way to the tab. Without
   * it, the team's feed (`team-detail`), whose `?type=ride` links keep working.
   */
  kind?: PublicationListKind
}

export function PublicationListPage({ kind }: PublicationListPageProps = {}) {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { filters, setFilters, team, publications, totalPages } = usePublicationListData(
    teamSlug,
    kind
  )
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const { data: teamData, isLoading: isLoadingTeam } = team
  const { data: publicationsData, isLoading: isLoadingPublications } = publications

  useCanonicalPath(teamData ? listPath(teamData.slug, kind) : undefined)

  const text =
    kind === 'ride'
      ? {
          title: t('teams.detail.tabs.rides'),
          empty: t('teams.rides.list.empty'),
          emptyDescription: t('teams.rides.list.emptyDescription'),
        }
      : kind === 'trip'
        ? {
            title: t('teams.detail.tabs.trips'),
            empty: t('teams.trips.list.empty'),
            emptyDescription: t('teams.trips.list.emptyDescription'),
          }
        : {
            title: t('teams.publications.list.title'),
            empty: t('teams.publications.list.empty'),
            emptyDescription: t('teams.publications.list.emptyDescription'),
          }

  if (isLoadingTeam) {
    return <LoadingPage message={text.title} />
  }

  if (!teamData) {
    // A team that does not exist, or that this visitor may not see, sends them to the list. A
    // server or network failure says so and offers to retry: the team is probably still there.
    const status = apiErrorStatus(team.error)
    if (team.isError && (status === undefined || status >= 500)) {
      return (
        <Container size="xl" py="xl">
          <QueryStateBoundary
            isLoading={false}
            isError
            error={team.error}
            onRetry={() => void team.refetch()}
          >
            {null}
          </QueryStateBoundary>
        </Container>
      )
    }
    return <Navigate to={paths.teams()} replace />
  }

  // A tab whose module the team switched off has nothing to list: back to the team page, as its
  // tab bar no longer offers it (useTeamNavItems).
  if (kind && !kindEnabled(teamData, kind)) {
    return <Navigate to={paths.team(teamData.slug)} replace />
  }

  const canCreate = teamData.role === 'ADMIN' || teamData.role === 'ORGANIZER'

  const allCreateMenuItems = [
    ...(teamData.enableRides && teamData.enableRoutes
      ? [{ kind: 'ride', path: paths.rideNew(teamSlug!), label: t('rides.create.title') }]
      : []),
    ...(teamData.enablePosts
      ? [{ kind: 'post', path: paths.postNew(teamSlug!), label: t('posts.create.title') }]
      : []),
    ...(teamData.enableTrips && teamData.enableRoutes
      ? [{ kind: 'trip', path: paths.tripNew(teamSlug!), label: t('trips.create.title') }]
      : []),
    ...(teamData.enableRoutes
      ? [{ kind: 'route', path: paths.routeNew(teamSlug!), label: t('routes.create.title') }]
      : []),
  ]
  // On a tab, only that kind is created from its header.
  const createMenuItems = kind
    ? allCreateMenuItems.filter((item) => item.kind === kind)
    : allCreateMenuItems

  const primaryCreate = createMenuItems[0]

  // Tags belong to one kind of content (plan D3): the mixed feed has no tag filter (D13), a feed
  // narrowed to rides, posts or trips — the team's dedicated list of that kind — has one.
  const tagTarget = publicationFilterToType[filters.filter]
  // The type and scope selects narrow the feed just as much as the search box does, so an empty
  // result under either of them is a filtered state — and must offer a way out. A `?tags=` on the
  // mixed feed is neither sent nor shown, so it filters nothing and does not count.
  // On a tab, the kind is the page's own, not a filter to clear.
  const hasNonSearchFilters =
    (!kind && filters.filter !== 'all') ||
    filters.scope !== 'all' ||
    (!!tagTarget && !!filters.tags?.length)
  const hasFiltersOrSearch = !!search || hasNonSearchFilters
  const clearFilters = () => {
    setSearch('')
    setFilters({ search: undefined, filter: 'all', scope: 'all', tags: undefined, page: 0 })
  }

  return (
    <TeamLayout team={teamData} currentTab={kind ? `${kind}s` : 'publications'}>
      <Stack gap="lg">
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>{text.title}</Title>
          <Group gap="xs">
            {canCreate && primaryCreate && (
              <Button.Group>
                <Button
                  component={PrefetchLink}
                  to={primaryCreate.path}
                  leftSection={<IconPlus size={16} />}
                >
                  {primaryCreate.label}
                </Button>
                {createMenuItems.length > 1 && (
                  <Menu position="bottom-end">
                    <Menu.Target>
                      <Button px="xs" aria-label={t('aria.createOther')}>
                        <IconChevronDown size={16} />
                      </Button>
                    </Menu.Target>
                    <Menu.Dropdown>
                      {createMenuItems.map((item) => (
                        <Menu.Item key={item.path} component={PrefetchLink} to={item.path}>
                          {item.label}
                        </Menu.Item>
                      ))}
                    </Menu.Dropdown>
                  </Menu>
                )}
              </Button.Group>
            )}
          </Group>
        </Group>

        {/* Search and Filter */}
        <Group justify="space-between" align="center" gap="sm">
          <SearchInput
            id="publications-search"
            value={search}
            onChange={setSearch}
            placeholder={t('teams.publications.list.search.placeholder')}
            style={{ flex: 1 }}
            fullWidth
          />
          <Group gap="xs">
            <PublicationScopeControl
              value={filters.scope}
              onChange={(scope) => setFilters({ scope, page: 0 })}
            />
            {!kind && (
              <Select
                value={filters.filter}
                onChange={(value) => {
                  if (value) {
                    // A ride tag means nothing to a post: switching kind drops the selection.
                    setFilters({ filter: value as PublicationFilterValue, tags: undefined })
                  }
                }}
                data={[
                  { value: 'all', label: t('teams.publications.list.filter.all') },
                  ...(teamData?.enableRides && teamData?.enableRoutes
                    ? [{ value: 'ride', label: t('teams.publications.list.filter.ride') }]
                    : []),
                  ...(teamData?.enablePosts
                    ? [{ value: 'post', label: t('teams.publications.list.filter.post') }]
                    : []),
                  ...(teamData?.enableTrips && teamData?.enableRoutes
                    ? [{ value: 'trip', label: t('teams.publications.list.filter.trip') }]
                    : []),
                ]}
                aria-label={t('teams.publications.list.filter.label')}
                w={{ base: 120, xs: 150 }}
                allowDeselect={false}
              />
            )}
          </Group>
        </Group>

        {tagTarget && (
          <TagFilter
            teamSlug={teamData.slug}
            type={tagTarget}
            value={filters.tags}
            onChange={(tags) => setFilters({ tags })}
          />
        )}

        <ResultCount
          total={publicationsData?.total}
          resource={kind ? `${kind}s` : 'publications'}
        />

        {/* Publications List */}
        {isLoadingPublications ? (
          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            {[...Array(6)].map((_, i) => (
              <PublicationCardSkeleton key={i} />
            ))}
          </SimpleGrid>
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          <Stack gap="xl">
            <SimpleGrid ref={listTopRef} cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {publicationsData.publications.map((publication) => (
                <PublicationCard
                  key={publication.id}
                  publication={publication}
                  showTeam={false}
                  actions={
                    <PublicationCardActions publication={publication} canManage={canCreate} />
                  }
                />
              ))}
            </SimpleGrid>

            <Box>
              <Pagination
                currentPage={filters.page}
                totalPages={totalPages}
                onPageChange={(page) => {
                  setFilters({ page })
                  scrollToListTop()
                }}
              />
            </Box>
          </Stack>
        ) : filters.page > 0 ? (
          <OutOfRangeState onFirstPage={() => setFilters({ page: 0 })} />
        ) : (
          <EmptyState
            variant={hasFiltersOrSearch ? 'filtered' : 'absolute'}
            icon={hasFiltersOrSearch ? <IconSearchOff size={48} /> : <IconNews size={48} />}
            title={hasFiltersOrSearch ? t('noResults') : text.empty}
            description={
              hasFiltersOrSearch
                ? t('teams.publications.list.search.noResultsDescription')
                : text.emptyDescription
            }
            actions={
              hasFiltersOrSearch ? (
                <Button variant="light" onClick={clearFilters}>
                  {hasNonSearchFilters ? t('common.clearFilters') : t('common.clearSearch')}
                </Button>
              ) : undefined
            }
          />
        )}
      </Stack>
    </TeamLayout>
  )
}

/** Where a list lives: the team page for the feed, its own tab for one kind. */
function listPath(teamSlug: string, kind: PublicationListKind | undefined): string {
  if (kind === 'ride') return paths.teamRides(teamSlug)
  if (kind === 'trip') return paths.teamTrips(teamSlug)
  return paths.team(teamSlug)
}

/** Same gate as the feed's type select and the team's tab bar: a ride or a trip needs a route. */
function kindEnabled(team: TeamDetailDto, kind: PublicationListKind): boolean {
  return kind === 'ride'
    ? !!team.enableRides && !!team.enableRoutes
    : !!team.enableTrips && !!team.enableRoutes
}

/** The « Sorties » tab of a team (`teamRides`). */
export function TeamRidesPage() {
  return <PublicationListPage kind="ride" />
}

/** The « Voyages » tab of a team (`teamTrips`). */
export function TeamTripsPage() {
  return <PublicationListPage kind="trip" />
}
