import { useState, useCallback } from 'react'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { IconMap } from '@tabler/icons-react'
import { Box, Center, Paper, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import {
  useListAllRoutes,
  listAllRoutes,
  getListAllRoutesQueryKey,
} from '@/api/endpoints/routes/routes'
import type { ListAllRoutesParams } from '@/api/dto'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'
import { Pagination } from '../../components/common/Pagination'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { HomeLayout } from '../../components/home/HomeLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'

export function AllRoutesPage() {
  const { t } = useTranslation()
  const pageSize = 20

  // Filter state - all in one object
  const [filters, setFilters] = useState<ListAllRoutesParams>({
    page: 0,
    size: pageSize,
  })
  const [filtersOpen, setFiltersOpen] = useState(false)

  const {
    data: routesData,
    isLoading,
    isError,
  } = useListAllRoutes(filters, {
    query: { placeholderData: keepPreviousData },
  })

  const handleFiltersChange = (newFilters: ListAllRoutesParams) => {
    setFilters({ ...newFilters, size: pageSize })
  }

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }))
  }

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListAllRoutesQueryKey({ ...filters, page: prefetchPageNum }),
      queryFn: () => listAllRoutes({ ...filters, page: prefetchPageNum }),
    }),
    [filters]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page ?? 0,
    pageSize,
    totalItems: routesData?.total ?? 0,
    prefetchPage,
  })

  const hasFiltersOrSearch =
    filters.search ||
    filters.minDistance !== undefined ||
    filters.maxDistance !== undefined ||
    filters.minElevationGain !== undefined ||
    filters.maxElevationGain !== undefined ||
    filters.hilliness !== undefined ||
    filters.surfaceType !== undefined ||
    filters.windDirection !== undefined

  return (
    <HomeLayout currentTab="routes">
      {/* Routes Section */}
      <Stack my="lg" gap="lg">
        <Title order={2}>{t('routes.list.title')}</Title>

        {/* Filter Panel */}
        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        {/* Loading State */}
        {isLoading ? (
          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            <RouteCardSkeleton count={6} />
          </SimpleGrid>
        ) : isError ? (
          /* Error State */
          <Paper shadow="xs" withBorder py="xl">
            <Center>
              <Stack align="center" gap="md">
                <IconMap size={48} color="var(--mantine-color-red-filled)" />
                <Title order={3}>{t('error.loading')}</Title>
              </Stack>
            </Center>
          </Paper>
        ) : routesData?.routes && routesData.routes.length > 0 ? (
          /* Routes Grid */
          <>
            <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {routesData.routes.map((route) => {
                return <RouteCard key={route.id} route={route} showTeam={true} />
              })}
            </SimpleGrid>

            <Box mt="xl">
              <Pagination
                currentPage={filters.page ?? 0}
                totalPages={totalPages}
                onPageChange={handlePageChange}
              />
            </Box>
          </>
        ) : (
          /* Empty State */
          <Paper shadow="xs" withBorder py="xl">
            <Center>
              <Stack align="center" gap="md">
                <IconMap size={48} color="var(--mantine-color-dimmed)" />
                <Title order={3}>
                  {hasFiltersOrSearch ? t('routes.list.noResults') : t('routes.list.empty.title')}
                </Title>
                {!hasFiltersOrSearch && (
                  <Text c="dimmed">{t('routes.list.empty.description')}</Text>
                )}
              </Stack>
            </Center>
          </Paper>
        )}
      </Stack>
    </HomeLayout>
  )
}
