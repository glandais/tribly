import { Navigate, useParams } from 'react-router-dom'
import { useTranslation } from 'react-i18next'
import { Group, Stack, Title } from '@mantine/core'
import { useGetTeam } from '@/api/endpoints/teams/teams'
import { paths } from '../../config/paths'
import { LoadingPage } from '../../components/common/LoadingSpinner'
import { TeamLayout } from '../../components/team/TeamLayout'
import { teamRoutesTilesUrl } from '../../components/map/mapConstants'
import { RoutesTileMap } from '../../components/route/RoutesTileMap'
import { RouteViewToggle } from '../../components/route/RouteViewToggle'
import { useCanonicalPath } from '../../hooks/useCanonicalPath'

export function RoutesMapPage() {
  const { teamSlug } = useParams<{ teamSlug: string }>()
  const { t } = useTranslation()

  const { data: team, isLoading } = useGetTeam(teamSlug!, { query: { enabled: !!teamSlug } })

  useCanonicalPath(team ? paths.routesMap(team.slug) : undefined)

  if (isLoading) {
    return <LoadingPage message={t('routes.list.title')} />
  }

  if (!team) {
    return <Navigate to={paths.teams()} replace />
  }

  return (
    <TeamLayout team={team} currentTab="routes">
      <Stack py="lg">
        <Group justify="space-between">
          <Title order={2}>{t('routes.list.title')}</Title>
          <RouteViewToggle current="map" teamSlug={team.slug} />
        </Group>

        <RoutesTileMap tilesUrl={teamRoutesTilesUrl(team.slug)} />
      </Stack>
    </TeamLayout>
  )
}
