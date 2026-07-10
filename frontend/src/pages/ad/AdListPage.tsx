import { useCallback } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconPlus } from '@tabler/icons-react'
import {
  Button,
  Select,
  Stack,
  Group,
  Title,
  Text,
  SimpleGrid,
  Box,
  Paper,
  Center,
  Space,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListAds, listAds, getListAdsQueryKey } from '../../api/endpoints/ads/ads'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useUrlFilters } from '../../hooks/useUrlFilters'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import { adFiltersSchema, adFiltersAlias } from '../../hooks/filters/adFilters'
import { AdCard, AdCardSkeleton } from '../../components/ad'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { Pagination } from '../../components/common/Pagination'
import { SearchInput } from '../../components/common/SearchInput'
import { TeamLayout } from '../../components/team/TeamLayout'
import { paths } from '@/config/paths'
import { AdType } from '../../api/dto'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function AdListPage() {
  const { t } = useTranslation()
  const { teamSlug } = useParams<{ teamSlug: string }>()

  const { filters, setFilters } = useUrlFilters({
    schema: adFiltersSchema,
    alias: adFiltersAlias,
  })
  const commitSearch = useCallback(
    (value: string) => setFilters({ search: value || undefined }),
    [setFilters]
  )
  const [search, setSearch] = useDebouncedSearch(filters.search ?? '', commitSearch)
  const { listTopRef, scrollToListTop } = useScrollToListTop()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const {
    data: adsResponse,
    isLoading: isLoadingAds,
    isFetching,
  } = useListAds(teamSlug!, filters, {
    query: { enabled: !!teamSlug, placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAdsQueryKey(teamSlug!, { ...filters, page: prefetchPageNum }),
      queryFn: () => listAds(teamSlug!, { ...filters, page: prefetchPageNum }),
    }),
    [teamSlug, filters]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize: filters.size,
    totalItems: adsResponse?.total ?? 0,
    prefetchPage,
  })

  useCanonicalPath(team ? paths.ads(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('loading')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const isMember = !!team.role
  const ads = adsResponse?.ads || []

  return (
    <TeamLayout team={team} currentTab="ads">
      {/* Header */}
      <Group justify="space-between" align="center" wrap="wrap">
        <Title order={2}>{t('ads.title')}</Title>
        {isMember && (
          <Button component={Link} to={paths.adNew(teamSlug!)} leftSection={<IconPlus size={20} />}>
            {t('ads.list.createAd')}
          </Button>
        )}
      </Group>
      <Space h="md" />

      {/* Filters */}
      <Group align="flex-end" wrap="wrap">
        <SearchInput
          id="ads-search"
          value={search}
          onChange={setSearch}
          placeholder={t('ads.list.search.placeholder')}
          label={t('ads.list.search.label')}
          style={{ flex: 1, minWidth: 200 }}
        />
        <Select
          w={{ base: '100%', sm: 180 }}
          value={filters.adType ?? 'ALL'}
          onChange={(value: string | null) =>
            setFilters({ adType: value && value !== 'ALL' ? (value as AdType) : undefined })
          }
          placeholder={t('ads.list.filterByType')}
          data={[
            { value: 'ALL', label: t('ads.list.allTypes') },
            { value: AdType.SALE, label: t('ads.adType.SALE') },
            { value: AdType.RENTAL, label: t('ads.adType.RENTAL') },
            { value: AdType.WANTED, label: t('ads.adType.WANTED') },
          ]}
        />
      </Group>

      <Space h="md" />

      {/* Content */}
      {isLoadingAds ? (
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
          {Array.from({ length: 6 }).map((_, i) => (
            <AdCardSkeleton key={i} />
          ))}
        </SimpleGrid>
      ) : ads.length === 0 ? (
        <Paper withBorder p="xl" radius="md">
          <Center>
            <Stack align="center" gap="sm">
              <Title order={3}>{t('ads.list.empty.title')}</Title>
              <Text c="dimmed">
                {filters.search || filters.adType
                  ? t('ads.list.noResults')
                  : t('ads.list.empty.member')}
              </Text>
            </Stack>
          </Center>
        </Paper>
      ) : (
        <Stack>
          <SimpleGrid
            ref={listTopRef}
            cols={{ base: 1, sm: 2, lg: 3 }}
            spacing="lg"
            style={{ opacity: isFetching ? 0.5 : 1 }}
          >
            {ads.map((ad) => (
              <AdCard key={ad.id} ad={ad} />
            ))}
          </SimpleGrid>

          {totalPages > 1 && (
            <Box mt="md">
              <Pagination
                currentPage={filters.page}
                totalPages={totalPages}
                onPageChange={(page) => {
                  setFilters({ page })
                  scrollToListTop()
                }}
              />
            </Box>
          )}
        </Stack>
      )}
    </TeamLayout>
  )
}
