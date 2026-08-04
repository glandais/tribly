import { useCallback } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { PrefetchLink } from '@/components/common/PrefetchLink'
import { useTranslation } from 'react-i18next'
import { IconPlus, IconNews, IconChevronDown, IconSearchOff } from '@tabler/icons-react'
import { Button, Menu, Select, Stack, Group, Title, Box, SimpleGrid } from '@mantine/core'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { TeamLayout } from '../../components/team/TeamLayout'
import { EmptyState } from '../../components/common/EmptyState'
import { Pagination } from '../../components/common/Pagination'
import { ResultCount } from '../../components/common/ResultCount'
import { PublicationScopeControl } from '../../components/home/PublicationScopeControl'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import { type PublicationFilterValue } from '../../hooks/filters/publicationFilters'
import { SearchInput } from '../../components/common/SearchInput'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { usePublicationListData } from './publicationListData'

export function PublicationListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { filters, setFilters, team, publications, totalPages } = usePublicationListData(teamSlug)
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const { data: teamData, isLoading: isLoadingTeam } = team
  const { data: publicationsData, isLoading: isLoadingPublications } = publications

  useCanonicalPath(teamData ? paths.team(teamData.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('teams.publications.list.title')} />
  }

  if (!teamData) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = teamData.role === 'ADMIN' || teamData.role === 'ORGANIZER'

  const createMenuItems = [
    ...(teamData.enableRides && teamData.enableRoutes
      ? [{ path: paths.rideNew(teamSlug!), label: t('rides.create.title') }]
      : []),
    ...(teamData.enablePosts
      ? [{ path: paths.postNew(teamSlug!), label: t('posts.create.title') }]
      : []),
    ...(teamData.enableTrips && teamData.enableRoutes
      ? [{ path: paths.tripNew(teamSlug!), label: t('trips.create.title') }]
      : []),
    ...(teamData.enableRoutes
      ? [{ path: paths.routeNew(teamSlug!), label: t('routes.create.title') }]
      : []),
  ]

  const primaryCreate = createMenuItems[0]

  // The type and scope selects narrow the feed just as much as the search box does, so an empty
  // result under either of them is a filtered state — and must offer a way out.
  const hasNonSearchFilters = filters.filter !== 'all' || filters.scope !== 'all'
  const hasFiltersOrSearch = !!search || hasNonSearchFilters
  const clearFilters = () => {
    setSearch('')
    setFilters({ search: undefined, filter: 'all', scope: 'all', page: 0 })
  }

  return (
    <TeamLayout team={teamData} currentTab="publications">
      <Stack gap="lg">
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>{t('teams.publications.list.title')}</Title>
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
                      <Button px="xs">
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
            <Select
              value={filters.filter}
              onChange={(value) => {
                if (value) {
                  setFilters({ filter: value as PublicationFilterValue })
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
          </Group>
        </Group>

        <ResultCount total={publicationsData?.total} resource="publications" />

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
                <PublicationCard key={publication.id} publication={publication} showTeam={false} />
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
        ) : (
          <EmptyState
            variant={hasFiltersOrSearch ? 'filtered' : 'absolute'}
            icon={hasFiltersOrSearch ? <IconSearchOff size={48} /> : <IconNews size={48} />}
            title={hasFiltersOrSearch ? t('noResults') : t('teams.publications.list.empty')}
            description={
              hasFiltersOrSearch
                ? t('teams.publications.list.search.noResultsDescription')
                : t('teams.publications.list.emptyDescription')
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
