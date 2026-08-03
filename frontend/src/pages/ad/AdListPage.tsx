import { useCallback, useMemo } from 'react'
import { Link, Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconPlus, IconSearchOff, IconTag } from '@tabler/icons-react'
import {
  Button,
  NumberInput,
  Select,
  Stack,
  Group,
  Text,
  Title,
  SimpleGrid,
  Box,
  Space,
} from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { useListAds, listAds, getListAdsQueryKey } from '../../api/endpoints/ads/ads'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useUrlFilters } from '../../hooks/useUrlFilters'
import { useDebouncedSearch } from '../../hooks/useDebouncedSearch'
import { useScrollToListTop } from '../../hooks/useScrollToListTop'
import {
  adFiltersSchema,
  adFiltersAlias,
  isAdFiltered,
  adApiParams,
} from '../../hooks/filters/adFilters'
import { AdCard, AdCardSkeleton } from '../../components/ad'
import {
  AD_SORT_OPTIONS,
  adSortOptionByValue,
  adSortOptionOf,
} from '../../components/ad/adSortOptions'
import { EmptyState } from '../../components/common/EmptyState'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { Pagination } from '../../components/common/Pagination'
import { ResultCount } from '../../components/common/ResultCount'
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
  // A card needs `excerpt`, `thumbnailUrl` and `images`, all of which COMPACT keeps — it only
  // drops the markdown body, the attachments and every picture past the first.
  const apiParams = useMemo(() => adApiParams(filters), [filters])

  const {
    data: adsResponse,
    isLoading: isLoadingAds,
    isFetching,
  } = useListAds(teamSlug!, apiParams, {
    query: { enabled: !!teamSlug, placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAdsQueryKey(teamSlug!, { ...apiParams, page: prefetchPageNum }),
      queryFn: () => listAds(teamSlug!, { ...apiParams, page: prefetchPageNum }),
    }),
    [teamSlug, apiParams]
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
  const hasFiltersOrSearch = isAdFiltered(filters)
  // The sort survives: clearing brings the list back into view, it does not reorder what the
  // reader chose to read.
  const clearFilters = () => {
    setSearch('')
    setFilters({
      search: undefined,
      adType: undefined,
      minPrice: undefined,
      maxPrice: undefined,
      page: 0,
    })
  }

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
        <Select
          w={{ base: '100%', sm: 220 }}
          label={t('ads.sort.title')}
          allowDeselect={false}
          value={adSortOptionOf(filters.sortBy, filters.sortDir)}
          onChange={(value) => {
            const option = value ? adSortOptionByValue(value) : undefined
            if (option) setFilters({ sortBy: option.sortBy, sortDir: option.sortDir })
          }}
          data={AD_SORT_OPTIONS.map((option) => ({
            value: option.value,
            label: t(
              `ads.sort.${option.value satisfies 'newest' | 'oldest' | 'priceAsc' | 'priceDesc' | 'nameAsc' | 'nameDesc'}`
            ),
          }))}
        />
      </Group>

      <Space h="xs" />

      {/* Price bounds. Either one drops the ads with no price — said in the hint, because a
          "Prix à négocier" ad vanishing from a filtered list is otherwise unexplained. */}
      <Group align="flex-end" wrap="wrap">
        <NumberInput
          w={{ base: '100%', sm: 160 }}
          label={t('ads.list.filters.minPrice')}
          placeholder={t('ads.list.filters.pricePlaceholder')}
          min={0}
          step={10}
          suffix=" €"
          value={filters.minPrice ?? ''}
          onChange={(value) =>
            setFilters({ minPrice: typeof value === 'number' ? value : undefined })
          }
        />
        <NumberInput
          w={{ base: '100%', sm: 160 }}
          label={t('ads.list.filters.maxPrice')}
          placeholder={t('ads.list.filters.pricePlaceholder')}
          min={0}
          step={10}
          suffix=" €"
          value={filters.maxPrice ?? ''}
          onChange={(value) =>
            setFilters({ maxPrice: typeof value === 'number' ? value : undefined })
          }
        />
        <Text size="xs" c="dimmed" pb={8}>
          {t('ads.list.filters.priceHint')}
        </Text>
      </Group>

      <Space h="md" />

      <ResultCount total={adsResponse?.total} resource="ads" />

      {/* Content */}
      {isLoadingAds ? (
        <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
          {Array.from({ length: 6 }).map((_, i) => (
            <AdCardSkeleton key={i} />
          ))}
        </SimpleGrid>
      ) : ads.length === 0 ? (
        <EmptyState
          variant={hasFiltersOrSearch ? 'filtered' : 'absolute'}
          icon={hasFiltersOrSearch ? <IconSearchOff size={48} /> : <IconTag size={48} />}
          title={hasFiltersOrSearch ? t('ads.list.noResultsTitle') : t('ads.list.empty.title')}
          description={hasFiltersOrSearch ? t('ads.list.noResults') : t('ads.list.empty.member')}
          actions={
            hasFiltersOrSearch ? (
              <Button variant="light" onClick={clearFilters}>
                {t('common.clearFilters')}
              </Button>
            ) : undefined
          }
        />
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
