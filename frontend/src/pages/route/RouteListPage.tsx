import { useCallback } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { keepPreviousData } from '@tanstack/react-query'
import { paths } from '../../config/paths'
import { IconPlus } from '@tabler/icons-react'
import { Button, Group, Stack, Title } from '@mantine/core'
import { useListRoutes, listRoutes, getListRoutesQueryKey } from '@/api/endpoints/routes/routes'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import type { ListRoutesParams } from '@/api/dto'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { usePaginatedQuery } from '../../hooks/usePaginatedQuery'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { UploadGpxFiles } from '../../components/route/UploadGpxFiles'

export function RouteListPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()

  const {
    filters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    handlePageChange,
    hasFiltersOrSearch,
    pageSize,
  } = useRouteFilters<ListRoutesParams>()

  const { data: team, isLoading: isLoadingTeam } = useGetTeam(teamSlug!, {
    query: { enabled: !!teamSlug },
  })
  const { data: routesData, isLoading: isLoadingRoutes } = useListRoutes(teamSlug!, filters, {
    query: { enabled: !!teamSlug, placeholderData: keepPreviousData },
  })

  const prefetchPage = useCallback(
    (prefetchPageNum: number) => ({
      queryKey: getListRoutesQueryKey(teamSlug, { ...filters, page: prefetchPageNum }),
      queryFn: () => listRoutes(teamSlug!, { ...filters, page: prefetchPageNum }),
    }),
    [teamSlug, filters]
  )

  const { totalPages } = usePaginatedQuery({
    page: filters.page ?? 0,
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
          {canCreateRoute && (
            <Group gap="sm">
              <UploadGpxFiles teamSlug={teamSlug!} />
              <Button
                component="a"
                href={paths.routeNew(teamSlug!)}
                leftSection={<IconPlus size={16} />}
              >
                {t('routes.create.title')}
              </Button>
            </Group>
          )}
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
        />

        <RouteListContent
          routes={routesData?.routes}
          isLoading={isLoadingRoutes}
          showTeam={false}
          hasFiltersOrSearch={hasFiltersOrSearch}
          currentPage={filters.page ?? 0}
          totalPages={totalPages}
          onPageChange={handlePageChange}
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
