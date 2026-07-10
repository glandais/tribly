import { useCallback, useMemo } from 'react'
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
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useUrlFilters } from '../../hooks/useUrlFilters'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useMembershipDefault } from '../../hooks/useMembershipDefault'
import { useAuth } from '../../hooks/useAuth'
import {
  publicationFilterToType,
  type PublicationFilterValue,
} from '../../hooks/filters/publicationFilters'
import {
  makeHomeFiltersSchema,
  homeFiltersAlias,
  homeFiltersAlwaysSerialize,
} from '../../hooks/filters/homeFilters'
import { membershipToMinRole, type MembershipFilterValue } from '../../hooks/filters/membership'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import { useAppName } from '../../hooks/useAppName'

export function HomePage() {
  const { t } = useTranslation()
  const appName = useAppName()
  const pinnedTeamSlug = getPinnedTeamSlug()
  const { isAuthenticated } = useAuth()

  const membershipDefault = useMembershipDefault()
  const schema = useMemo(() => makeHomeFiltersSchema(membershipDefault), [membershipDefault])

  const { filters, setFilters } = useUrlFilters({
    schema,
    alias: homeFiltersAlias,
    alwaysSerialize: homeFiltersAlwaysSerialize,
  })
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)

  // `filter` and `membership` are the page's own values; the API wants a PublicationType and a
  // MinRole.
  const apiParams = useMemo(
    () => ({
      search: filters.search,
      page: filters.page,
      size: filters.size,
      type: publicationFilterToType[filters.filter],
      minRole: membershipToMinRole[filters.membership],
    }),
    [filters]
  )

  const {
    data: publicationsData,
    isLoading,
    isError,
  } = useListAllPublications(apiParams)

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllPublicationsQueryKey({ ...apiParams, page: prefetchPageNum }),
      queryFn: () => listAllPublications({ ...apiParams, page: prefetchPageNum }),
    }),
    [apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
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
            onChange={setSearch}
            placeholder={t('home.feed.search.placeholder')}
            label={t('home.feed.search.label')}
            style={{ flex: 1, minWidth: 200 }}
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
              { value: 'ride', label: t('teams.publications.list.filter.ride') },
              { value: 'post', label: t('teams.publications.list.filter.post') },
              { value: 'trip', label: t('teams.publications.list.filter.trip') },
            ]}
            aria-label={t('teams.publications.list.filter.label')}
            w={{ base: '100%', sm: 160 }}
          />
          {isAuthenticated && (
            <Select
              value={filters.membership}
              onChange={(value) => setFilters({ membership: value as MembershipFilterValue })}
              data={[
                { value: 'all', label: t('filters.membership.all') },
                { value: 'member', label: t('roles.MEMBER') },
                { value: 'organizer', label: t('roles.ORGANIZER') },
                { value: 'admin', label: t('roles.ADMIN') },
              ]}
              aria-label={t('filters.membership.label')}
              allowDeselect={false}
              w={{ base: '100%', sm: 180 }}
            />
          )}
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

            <Pagination
              currentPage={filters.page}
              totalPages={totalPages}
              onPageChange={(page) => setFilters({ page })}
            />
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
