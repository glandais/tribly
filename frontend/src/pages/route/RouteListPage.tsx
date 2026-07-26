import { useCallback, useMemo } from 'react'
import { ListViewMode } from '@/api/dto'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { paths } from '../../config/paths'
import { IconPlus } from '@tabler/icons-react'
import { Button, Group, Stack, Title } from '@mantine/core'
import { useListRoutes, listRoutes, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import {
  resolveRouteDensity,
  routeFiltersSchema,
  routeFiltersAlias,
} from '../../hooks/filters/routeFilters'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { ResultCount } from '@/components/common/ResultCount'
import { RouteDensityToggle } from '@/components/route/RouteDensityToggle'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { UploadGpxFiles } from '../../components/route/UploadGpxFiles'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()

  const {
    filters,
    setFilters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    clearFilters,
    pageSize,
  } = useRouteFilters({ schema: routeFiltersSchema, alias: routeFiltersAlias })

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  // `density` is presentation: it must never reach the API.
  const apiParams = useMemo(() => {
    const params = { ...filters, view: ListViewMode.COMPACT }
    delete params.density
    return params
  }, [filters])

  const { data: routesData, isLoading: isLoadingRoutes } = useListRoutes(teamSlug!, apiParams, {
    query: { enabled: !!teamSlug, placeholderData: keepPreviousData },
  })

  const density = resolveRouteDensity(filters.density, routesData?.total)

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListRoutesQueryKey(teamSlug!, { ...apiParams, page: prefetchPageNum }),
      queryFn: () => listRoutes(teamSlug!, { ...apiParams, page: prefetchPageNum }),
    }),
    [teamSlug, apiParams]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page,
    pageSize,
    totalItems: routesData?.total ?? 0,
    prefetchPage,
  })

  useCanonicalPath(team ? paths.routes(team.slug) : undefined)

  if (isLoadingTeam) {
    return <LoadingPage message={t('routes.list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  const canCreateRoute = team.role === 'ADMIN' || team.role === 'ORGANIZER'

  return (
    <TeamLayout team={team} currentTab="routes">
      <Stack py="lg">
        <Group justify="space-between">
          <Title order={2}>{t('routes.list.title')}</Title>
          <Group gap="sm">
            {canCreateRoute && (
              <>
                <UploadGpxFiles team={team} />
                <Button
                  component="a"
                  href={paths.routeNew(teamSlug!)}
                  leftSection={<IconPlus size={16} />}
                >
                  {t('routes.create.title')}
                </Button>
              </>
            )}
            <RouteViewToggle current="list" teamSlug={team.slug} />
          </Group>
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        <Group justify="space-between" align="center">
          <ResultCount total={routesData?.total} resource="routes" />
          <RouteDensityToggle
            value={density}
            onChange={(value) => setFilters({ density: value })}
          />
        </Group>

        <RouteListContent
          density={density}
          routes={routesData?.routes}
          isLoading={isLoadingRoutes}
          showTeam={false}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page}
          totalPages={totalPages}
          onPageChange={handlePageChange}
          onClearFilters={clearFilters}
          emptyAction={
            canCreateRoute && !hasFiltersOrSearch ? (
              <Button component="a" href={paths.routeNew(teamSlug!)} mt="sm">
                {t('routes.create.title')}
              </Button>
            ) : undefined
          }
        />
      </Stack>
    </TeamLayout>
  )
}
