import { useState, useCallback } from 'react'
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
import { PublicationType } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { TeamLayout } from '../../components/team/TeamLayout'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { SearchInput } from '../../components/common/SearchInput'
import { paths } from '@/config/paths'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

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
  const pageSize = 12 // Changed to 12 for better grid alignment (divisible by 2, 3, 4)

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

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListPublicationsQueryKey(teamSlug!, {
        search: search || undefined,
        page: prefetchPageNum,
        size: pageSize,
        type: filterToType[filter],
      }),
      queryFn: () =>
        listPublications(teamSlug!, {
          search: search || undefined,
          page: prefetchPageNum,
          size: pageSize,
          type: filterToType[filter],
        }),
    }),
    [teamSlug, search, filter, pageSize]
  )

  const { totalPages } = usePaginatedQuery({
    page,
    pageSize,
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

  return (
    <TeamLayout team={team} currentTab="publications">
      <Stack gap="lg">
        <Group justify="space-between" align="center" wrap="wrap">
          <Title order={2}>{t('teams.publications.list.title')}</Title>
          <Group gap="xs">
            {canCreate && (
              <Button.Group>
                <Button
                  component={Link}
                  to={paths.rideNew(teamSlug!)}
                  leftSection={<IconPlus size={16} />}
                >
                  {t('rides.create.title')}
                </Button>
                <Menu position="bottom-end">
                  <Menu.Target>
                    <Button px="xs">
                      <IconChevronDown size={16} />
                    </Button>
                  </Menu.Target>
                  <Menu.Dropdown>
                    <Menu.Item component={Link} to={paths.rideNew(teamSlug!)}>
                      {t('rides.create.title')}
                    </Menu.Item>
                    <Menu.Item component={Link} to={paths.postNew(teamSlug!)}>
                      {t('posts.create.title')}
                    </Menu.Item>
                    {team.enableTrips && (
                      <Menu.Item component={Link} to={paths.tripNew(teamSlug!)}>
                        {t('trips.create.title')}
                      </Menu.Item>
                    )}
                    <Menu.Item component={Link} to={paths.routeNew(teamSlug!)}>
                      {t('routes.create.title')}
                    </Menu.Item>
                  </Menu.Dropdown>
                </Menu>
              </Button.Group>
            )}
          </Group>
        </Group>

        {/* Search and Filter */}
        <Group justify="space-between" align="center" gap="sm">
          <SearchInput
            id="publications-search"
            value={search}
            onChange={(value) => {
              setSearch(value)
              resetPage()
            }}
            placeholder={t('teams.publications.list.search.placeholder')}
            style={{ flex: 1 }}
            fullWidth
          />
          <Group gap="xs">
            <Select
              value={filter}
              onChange={(value) => {
                if (value) {
                  setFilter(value as FilterValue)
                  resetPage()
                }
              }}
              data={[
                { value: 'all', label: t('teams.publications.list.filter.all') },
                { value: 'ride', label: t('teams.publications.list.filter.ride') },
                { value: 'post', label: t('teams.publications.list.filter.post') },
                ...(team?.enableTrips
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
            <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {publicationsData.publications.map((publication) => (
                <PublicationCard key={publication.id} publication={publication} showTeam={false} />
              ))}
            </SimpleGrid>

            <Box>
              <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
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
