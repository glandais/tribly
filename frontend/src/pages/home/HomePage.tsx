import { useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import {
  Box,
  Button,
  Select,
  Stack,
  Title,
  Group,
  Paper,
  Text,
  Center,
  SimpleGrid,
} from '@mantine/core'
import { IconNews, IconSearchOff } from '@tabler/icons-react'
import { isSingleTeam } from '../../config/appConfig'
import type { RideDto } from '../../api/dto'
import { PublicationCard, PublicationCardSkeleton } from '../../components/card'
import { EmptyState } from '../../components/common/EmptyState'
import { Pagination } from '../../components/common/Pagination'
import { ResultCount } from '../../components/common/ResultCount'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import { useAuth } from '../../hooks/useAuth'
import type {
  PublicationFilterValue,
  PublicationScopeValue,
} from '../../hooks/filters/publicationFilters'
import type { MembershipFilterValue } from '../../hooks/filters/membership'
import { SearchInput } from '../../components/common/SearchInput'
import { HomeLayout } from '../../components/home/HomeLayout'
import { NextRideCard } from '../../components/home/NextRideCard'
import { PublicationScopeControl } from '../../components/home/PublicationScopeControl'
import { useAppName } from '../../hooks/useAppName'
import { useHomeFeedData } from './homeFeedData'

export function HomePage() {
  const { t } = useTranslation()
  const appName = useAppName()
  const { isAuthenticated } = useAuth()

  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const { membershipDefault, filters, setFilters, publications, totalPages, participations } =
    useHomeFeedData()
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)

  // An empty feed reads differently depending on whether anything is narrowing it down.
  const hasFiltersOrSearch =
    !!filters.search ||
    filters.filter !== 'all' ||
    filters.scope !== 'all' ||
    filters.membership !== membershipDefault
  const clearFilters = useCallback(() => {
    setSearch('')
    setFilters({
      search: undefined,
      filter: 'all',
      scope: 'all',
      membership: membershipDefault,
      page: 0,
    })
  }, [setSearch, setFilters, membershipDefault])

  // `filter` and `membership` are the page's own values; the API wants a PublicationType and a
  // MinRole — projected inside `useHomeFeedData` via `publicationApiParams`.
  const { data: publicationsData, isLoading, isError } = publications

  // "Ma prochaine sortie" — authenticated-only. Prefetched by the route for a session-carrying
  // SSR request (see routes.config.ts), so it's already in the initial HTML for a signed-in
  // visitor rather than rendering after hydration.
  const nextRide = participations.data?.publications?.find(
    (p): p is RideDto => p.type === 'RIDE' && (p as RideDto).registered
  )

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

        {nextRide && (
          <Box>
            <Title order={3} mb="xs">
              {t('home.nextRide.title')}
            </Title>
            <NextRideCard ride={nextRide} />
          </Box>
        )}

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
          <PublicationScopeControl
            value={filters.scope}
            onChange={(scope: PublicationScopeValue) => setFilters({ scope, page: 0 })}
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

        <ResultCount total={publicationsData?.total} resource="publications" />

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
            <SimpleGrid ref={listTopRef} cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {publicationsData.publications.map((publication) => (
                <PublicationCard
                  key={publication.id}
                  publication={publication}
                  showTeam={!isSingleTeam()}
                />
              ))}
            </SimpleGrid>

            <Pagination
              currentPage={filters.page}
              totalPages={totalPages}
              onPageChange={(page) => {
                setFilters({ page })
                scrollToListTop()
              }}
            />
          </Stack>
        ) : (
          /* Empty State */
          <EmptyState
            variant={hasFiltersOrSearch ? 'filtered' : 'absolute'}
            icon={hasFiltersOrSearch ? <IconSearchOff size={48} /> : <IconNews size={48} />}
            title={hasFiltersOrSearch ? t('home.feed.noResultsTitle') : t('home.feed.empty')}
            description={
              hasFiltersOrSearch ? t('home.feed.noResults') : t('home.feed.emptyDescription')
            }
            actions={
              hasFiltersOrSearch ? (
                <Button variant="light" onClick={clearFilters}>
                  {t('common.clearFilters')}
                </Button>
              ) : undefined
            }
          />
        )}
      </Stack>
    </HomeLayout>
  )
}
