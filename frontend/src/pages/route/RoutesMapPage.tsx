import { useMemo } from 'react'
import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Group, Stack, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { paths } from '../../config/paths'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { teamRoutesTilesUrl, toRouteTileFilters } from '../../components/map/mapConstants'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'
import { routeFiltersSchema, routeFiltersAlias } from '../../hooks/filters/routeFilters'
import { useRouteFilters } from '../../hooks/useRouteFilters'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RoutesMapPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()

  const { filters, filtersOpen, setFiltersOpen, handleFiltersChange } = useRouteFilters({
    schema: routeFiltersSchema,
    alias: routeFiltersAlias,
  })

  const { data: team, isLoading } = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  const tilesUrl = useMemo(
    () => (team ? teamRoutesTilesUrl(team.slug, toRouteTileFilters(filters)) : undefined),
    [team, filters]
  )

  useCanonicalPath(team ? paths.routesMap(team.slug) : undefined)

  if (isLoading) {
    return <LoadingPage message={t('routes.list.title')} />
  }

  if (!team || !tilesUrl) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={team} currentTab="routes">
      <Stack py="lg">
        <Group justify="space-between">
          <Title order={2}>{t('routes.list.title')}</Title>
          <RouteViewToggle current="map" teamSlug={team.slug} />
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
          showSort={false}
        />

        <RoutesTileMap tilesUrl={tilesUrl} />
      </Stack>
    </TeamLayout>
  )
}
