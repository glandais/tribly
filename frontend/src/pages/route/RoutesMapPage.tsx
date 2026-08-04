import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Group, Stack, Title } from '@mantine/core'
import { paths } from '../../config/paths'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { RouteFilterPanel } from '../../components/route/RouteFilterPanel'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'
import { useRoutesMapData } from './routesMapData'

export function RoutesMapPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()

  const {
    filters,
    filtersOpen,
    setFiltersOpen,
    handleFiltersChange,
    team: { data: team, isLoading },
    tilesUrl,
    bounds,
  } = useRoutesMapData(teamSlug)

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
          <Group gap="sm">
            <RouteViewToggle current="map" teamSlug={team.slug} />
          </Group>
        </Group>

        <RouteFilterPanel
          filters={filters}
          onFiltersChange={handleFiltersChange}
          isOpen={filtersOpen}
          onOpenChange={setFiltersOpen}
          showSort={false}
        />

        <RoutesTileMap
          tilesUrl={tilesUrl}
          bounds={bounds.data?.bounds}
          boundsPending={bounds.isLoading}
        />
      </Stack>
    </TeamLayout>
  )
}
