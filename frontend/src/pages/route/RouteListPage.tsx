import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { paths } from '../../config/paths'
import { IconPlus } from '@tabler/icons-react'
import { Button, Group, Stack, Title } from '@mantine/core'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { useRouteListData } from './routeListData'
import { resolveRouteDensity } from '../../hooks/filters/routeFilters'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RouteListContent } from '../../components/route/RouteListContent'
import { ResultCount } from '@/components/common/ResultCount'
import { RouteDensityToggle } from '@/components/route/RouteDensityToggle'
import { RouteDeadEnd } from '@/components/route/RouteDeadEnd'
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
    team: { data: team, isLoading: isLoadingTeam },
    routes: { data: routesData, isLoading: isLoadingRoutes },
    totalPages,
  } = useRouteListData(teamSlug)

  const density = resolveRouteDensity(filters.density, routesData?.total)

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
          deadEnd={
            <RouteDeadEnd
              filters={filters}
              teamSlug={teamSlug!}
              onSetFilters={setFilters}
              onClearFilters={clearFilters}
            />
          }
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
