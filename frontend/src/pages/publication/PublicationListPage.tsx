import { useCallback, useMemo } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconPlus, IconNews, IconChevronDown } from '@tabler/icons-react'
import {
  Button,
  Menu,
  Select,
  Stack,
  Group,
  Title,
  Paper,
  Text,
  Center,
  Box,
  SimpleGrid,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import {
  useListPublications,
  listPublications,
  getListPublicationsQueryKey,
} from '../../api/endpoints/publications/publications'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useUrlFilters } from '../../hooks/useUrlFilters'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import {
  publicationFiltersSchema,
  publicationFiltersAlias,
  publicationFilterToType,
  type PublicationFilterValue,
} from '../../hooks/filters/publicationFilters'
import { SearchInput } from '../../components/common/SearchInput'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function PublicationListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { filters, setFilters } = useUrlFilters({
    schema: publicationFiltersSchema,
    alias: publicationFiltersAlias,
  })
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  // `filter` is the page's own value; the API wants a PublicationType.
  const apiParams = useMemo(
    () => ({
      search: filters.search,
      page: filters.page,
      size: filters.size,
      type: publicationFilterToType[filters.filter],
    }),
    [filters]
  )

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: publicationsData, isLoading: isLoadingPublications } = useListPublications(
    teamSlug!,
    apiParams,
    { query: { enabled: !!teamSlug } }
  )

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListPublicationsQueryKey(teamSlug!, { ...apiParams, page: prefetchPageNum }),
      queryFn: () => listPublications(teamSlug!, { ...apiParams, page: prefetchPageNum }),
    }),
    [teamSlug, apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: publicationsData?.total ?? 0,
    prefetchPage,
  })

  useCanonicalPath(team ? paths.team(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('teams.publications.list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreate = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  const createMenuItems = [
    ...(team.enableRides && team.enableRoutes
      ? [{ path: paths.rideNew(teamSlug!), label: t('rides.create.title') }]
      : []),
    ...(team.enablePosts
      ? [{ path: paths.postNew(teamSlug!), label: t('posts.create.title') }]
      : []),
    ...(team.enableTrips && team.enableRoutes
      ? [{ path: paths.tripNew(teamSlug!), label: t('trips.create.title') }]
      : []),
    ...(team.enableRoutes
      ? [{ path: paths.routeNew(teamSlug!), label: t('routes.create.title') }]
      : []),
  ]

  const primaryCreate = createMenuItems[0]

  return (
    <TeamLayout team={team} currentTab="publications">
      <Stack gap="lg">
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>{t('teams.publications.list.title')}</Title>
          <Group gap="xs">
            {canCreate && primaryCreate && (
              <Button.Group>
                <Button
                  component={Link}
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
                        <Menu.Item key={item.path} component={Link} to={item.path}>
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
            <Select
              value={filters.filter}
              onChange={(value) => {
                if (value) {
                  setFilters({ filter: value as PublicationFilterValue })
                }
              }}
              data={[
                { value: 'all', label: t('teams.publications.list.filter.all') },
                ...(team?.enableRides && team?.enableRoutes
                  ? [{ value: 'ride', label: t('teams.publications.list.filter.ride') }]
                  : []),
                ...(team?.enablePosts
                  ? [{ value: 'post', label: t('teams.publications.list.filter.post') }]
                  : []),
                ...(team?.enableTrips && team?.enableRoutes
                  ? [{ value: 'trip', label: t('teams.publications.list.filter.trip') }]
                  : []),
              ]}
              aria-label={t('teams.publications.list.filter.label')}
              w={{ base: 120, xs: 150 }}
              allowDeselect={false}
            />
          </Group>
        </Group>

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
          <Paper withBorder p={48} radius="md">
            <Center>
              <Stack align="center" gap="md">
                <Box
                  p="lg"
                  style={{
                    backgroundColor: 'var(--mantine-color-gray-1)',
                    borderRadius: '50%',
                  }}
                >
                  <IconNews size={48} color="var(--mantine-color-dimmed)" />
                </Box>
                <Title order={3} ta="center">
                  {search ? t('noResults') : t('teams.publications.list.empty')}
                </Title>
                <Text c="dimmed" ta="center" maw={400}>
                  {search
                    ? t('teams.publications.list.search.noResultsDescription')
                    : t('teams.publications.list.emptyDescription')}
                </Text>
                {search && (
                  <Button variant="light" onClick={() => setSearch('')}>
                    {t('common.clearSearch')}
                  </Button>
                )}
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </TeamLayout>
  )
}
