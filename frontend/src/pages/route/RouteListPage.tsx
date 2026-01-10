import { useState } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { paths } from '../../config/paths'
import { IconPlus, IconMap } from '@tabler/icons-react'
import { Box, Button, Center, Group, Paper, SimpleGrid, Stack, Text, Title } from '@mantine/core'
import { useListRoutes } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import type { ListRoutesParams } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { RouteCard, RouteCardSkeleton } from '../../components/route/RouteCard'
import { Pagination } from '../../components/common/Pagination'
import { usePagination } from '../../hooks/usePagination'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()
  const pageSize = 20

  // Filter state - all in one object
  const [filters, setFilters] = useState<ListRoutesParams>({
    page: 0,
    size: pageSize,
  })
  const [filtersOpen, setFiltersOpen] = useState(false)

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: routesData, isLoading: isLoadingRoutes } = useListRoutes(teamSlug!, filters, {
    query: { enabled: !!teamSlug, placeholderData: keepPreviousData },
  })

  const handleFiltersChange = (newFilters: ListRoutesParams) => {
    setFilters({ ...newFilters, size: pageSize })
  }

  const handlePageChange = (page: number) => {
    setFilters((prev) => ({ ...prev, page }))
  }

  // Use usePagination only for totalPages calculation
  const { totalPages } = usePagination({
    pageSize,
    totalItems: routesData?.total ?? 0,
  })

  useCanonicalPath(team ? paths.routes(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('routes.list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreateRoute = team.role === 'ADMIN' || team.role === 'ORGANIZER'
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
    <TeamLayout team={team} currentTab="routes">
      <Stack py="lg" gap="lg">
        <Group justify="space-between">
          <Title order={2}>{t('routes.list.title')}</Title>
          {canCreateRoute && (
            <Button
              component="a"
              href={paths.routeNew(teamSlug!)}
              leftSection={<IconPlus size={16} />}
            >
              {t('routes.create.title')}
            </Button>
          )}
        </Group>

        {/* Filter Panel */}
        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        {/* Routes List */}
        {isLoadingRoutes ? (
          <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
            <RouteCardSkeleton count={6} />
          </SimpleGrid>
        ) : routesData && routesData.routes.length > 0 ? (
          <>
            <SimpleGrid cols={{ base: 1, sm: 2, lg: 3 }} spacing="lg">
              {routesData.routes.map((route) => (
                <RouteCard key={route.id} route={route} showTeam={false} />
              ))}
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
          <Paper shadow="xs" withBorder py="xl">
            <Center>
              <Stack align="center" gap="md">
                <IconMap size={48} color="var(--mantine-color-gray-5)" />
                <Title order={3}>
                  {hasFiltersOrSearch ? t('routes.list.noResults') : t('routes.list.empty.title')}
                </Title>
                {!hasFiltersOrSearch && (
                  <Text c="dimmed">{t('routes.list.empty.description')}</Text>
                )}
                {canCreateRoute && !hasFiltersOrSearch && (
                  <Button component="a" href={paths.routeNew(teamSlug!)} mt="sm">
                    {t('routes.create.title')}
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
