import { useState, useCallback } from 'react'
import { Navigate } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Box, Select, Stack, Title, Group, Paper, Text, Center, SimpleGrid } from '@mantine/core'
import { IconNews } from '@tabler/icons-react'
import { paths } from '../../config/paths'
import { getPinnedTeamSlug } from '../../config/appConfig'
import {
  useListAllPublications,
  listAllPublications,
  getListAllPublicationsQueryKey,
} from '../../api/endpoints/publications/publications'
import { PublicationType } from '@/api/dto'
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import { useAppName } from '../../hooks/useAppName'

type FilterValue = 'all' | 'ride' | 'post' | 'trip'

const filterToType: Record<FilterValue, PublicationType | undefined> = {
  all: undefined,
  ride: PublicationType.RIDE,
  post: PublicationType.POST,
  trip: PublicationType.TRIP,
}

export function HomePage() {
  const { t } = useTranslation()
  const appName = useAppName()
  const pinnedTeamSlug = getPinnedTeamSlug()
  const [page, setPage] = useState(0)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState<FilterValue>('all')
  const pageSize = 12

  const {
    data: publicationsData,
    isLoading,
    isError,
  } = useListAllPublications({
    search: search || undefined,
    page,
    size: pageSize,
    type: filterToType[filter],
  })

  const resetPage = () => setPage(0)

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllPublicationsQueryKey({
        search: search || undefined,
        page: prefetchPageNum,
        size: pageSize,
        type: filterToType[filter],
      }),
      queryFn: () =>
        listAllPublications({
          search: search || undefined,
          page: prefetchPageNum,
          size: pageSize,
          type: filterToType[filter],
        }),
    }),
    [search, filter, pageSize]
  )

  const { totalPages } = usePaginatedQuery({
    page,
    pageSize,
    totalItems: publicationsData?.total ?? 0,
    prefetchPage,
  })

  // Dedicated hostname (domain alias): root the app on the pinned team.
  if (pinnedTeamSlug) {
    return <Navigate to={paths.team(pinnedTeamSlug)} replace />
  }

  return (
    <HomeLayout currentTab="feed">
      <Stack>
        <Box>
          <Title order={2}>{t('welcome', { appName })}</Title>
          <Text c="dimmed" mt={4} mb="md">
            {t('home.subtitle')}
          </Text>

          {/* Publications Section */}
          <Title order={2}>{t('home.feed.title')}</Title>
        </Box>

        {/* Search and Filter */}
        <Group align="flex-end" wrap="wrap">
          <SearchInput
            id="publications-search"
            value={search}
            onChange={(value) => {
              setSearch(value)
              resetPage()
            }}
            placeholder={t('home.feed.search.placeholder')}
            label={t('home.feed.search.label')}
            style={{ flex: 1, minWidth: 200 }}
          />
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
              { value: 'trip', label: t('teams.publications.list.filter.trip') },
            ]}
            aria-label={t('teams.publications.list.filter.label')}
            w={{ base: '100%', sm: 160 }}
          />
        </Group>

        {/* Loading State */}
        {isLoading ? (
          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            {[...Array(6)].map((_, i) => (
              <PublicationCardSkeleton key={i} />
            ))}
          </SimpleGrid>
        ) : isError ? (
          /* Error State */
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconNews size={48} color="var(--mantine-color-red-filled)" />
                <Text fw={500}>{t('error.loading')}</Text>
              </Stack>
            </Center>
          </Paper>
        ) : publicationsData?.publications && publicationsData.publications.length > 0 ? (
          /* Publications Grid */
          <Stack>
            <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {publicationsData.publications.map((publication) => (
                <PublicationCard key={publication.id} publication={publication} showTeam={true} />
              ))}
            </SimpleGrid>

            <Pagination currentPage={page} totalPages={totalPages} onPageChange={setPage} />
          </Stack>
        ) : (
          /* Empty State */
          <Paper withBorder p="xl" radius="md">
            <Center>
              <Stack align="center" gap="sm">
                <IconNews size={48} color="var(--mantine-color-dimmed)" />
                <Text fw={500}>{search ? t('home.feed.noResults') : t('home.feed.empty')}</Text>
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </HomeLayout>
  )
}
