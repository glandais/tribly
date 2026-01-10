import { useState } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { IconPlus, IconNews, IconChevronDown } from '@tabler/icons-react'
import { Button, Menu, Select, Stack, Group, Title, Paper, Text, Center, Box } from '@mantine/core'
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

        {/* Search and Filter */}
        <Group gap="md" align="flex-end" wrap="wrap">
          <Box style={{ flex: 1, minWidth: 200 }}>
            <SearchInput
              id="publications-search"
              value={search}
              onChange={(value) => {
                setSearch(value)
                resetPage()
              }}
              placeholder={t('teams.publications.list.search.placeholder')}
              label={t('teams.publications.list.search.label')}
            />
          </Box>
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
            w={{ base: '100%', sm: 160 }}
          />
        </Group>

        {/* Publications List */}
        {isLoadingPublications ? (
          <Stack gap="md">
            {[...Array(3)].map((_, i) => (
              <PublicationCardSkeleton key={i} />
            ))}
          </Stack>
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          <Stack gap="md">
            {publicationsData.publications.map((publication) => (
              <PublicationCard key={publication.id} publication={publication} showTeam={false} />
            ))}

            <Box mt="xl">
              <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
            </Box>
          </Stack>
        ) : (
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconNews size={48} color="var(--mantine-color-gray-5)" />
                <Text fw={500}>{search ? t('noResults') : t('teams.publications.list.empty')}</Text>
                {!search && <Text c="dimmed">{t('teams.publications.list.emptyDescription')}</Text>}
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </TeamLayout>
  )
}
