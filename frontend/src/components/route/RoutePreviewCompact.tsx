import { useTranslation } from 'react-i18next'
import { Group, Loader, Text } from '@mantine/core'
import { useGetRoute } from '@/api/endpoints/routes/routes'

interface RoutePreviewCompactProps {
  routeSlug: string
  teamSlug: string
}

export function RoutePreviewCompact({ routeSlug, teamSlug }: RoutePreviewCompactProps) {
  const { t } = useTranslation()
  const { data: route, isLoading } = useGetRoute(teamSlug, routeSlug)

  if (isLoading)
    return (
      <Group gap="xs">
        <Loader size="xs" />
        <Text size="xs" c="dimmed">
          {t('loading')}
        </Text>
      </Group>
    )

  if (!route)
    return (
      <Text size="xs" c="red">
        {t('routes.preview.notFound')}
      </Text>
    )

  return (
    <Text size="xs" c="dimmed">
      <Text component="span" fw={500}>
        {route.name}
      </Text>
      <Text component="span" ml="xs">
        ({t('distance', { distance: (route.distance / 1000).toFixed(1) })},{' '}
        {t('elevation', { elevation: route.elevationGain })})
      </Text>
    </Text>
  )
}
