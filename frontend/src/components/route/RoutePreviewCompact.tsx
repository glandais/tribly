import { useTranslation } from 'react-i18next'
import { Group, Loader, Text } from '@mantine/core'
import type { RouteDetailDto } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'

interface RoutePreviewCompactProps {
  /**
   * The route to preview, already fetched by the parent. `RoutePreviewCompact` is only ever
   * rendered in a loop (`RideEditor`'s groups, `TripEditor`'s stages) — the parent fetches every
   * distinct route slug of the loop in one `getRoutesBulk` call and passes each entry down,
   * rather than every row issuing its own `getRoute`.
   */
  route: RouteDetailDto | undefined
  isLoading: boolean
}

export function RoutePreviewCompact({ route, isLoading }: RoutePreviewCompactProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()

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
        ({distance(route.distance)}, {elevation(route.elevationGain)})
      </Text>
    </Text>
  )
}
