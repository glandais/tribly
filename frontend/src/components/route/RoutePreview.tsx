import { useTranslation } from 'react-i18next'
import { IconArrowsMaximize, IconArrowUp } from '@tabler/icons-react'
import { Paper, Group, Text, Image, Stack, Loader } from '@mantine/core'
import { useGetRoute } from '@/api/endpoints/routes/routes'
import { useUnits } from '@/hooks/useUnits'

interface RoutePreviewProps {
  routeSlug: string
  teamSlug: string
}

export function RoutePreview({ routeSlug, teamSlug }: RoutePreviewProps) {
  const { t } = useTranslation()
  const { distance, elevation } = useUnits()
  const { data: route, isLoading } = useGetRoute(teamSlug, routeSlug)

  if (isLoading)
    return (
      <Group gap="xs">
        <Loader size="sm" />
        <Text size="sm" c="dimmed">
          {t('loading')}
        </Text>
      </Group>
    )

  if (!route)
    return (
      <Text size="sm" c="red">
        {t('routes.preview.notFound')}
      </Text>
    )

  return (
    <Paper withBorder p="sm" bg="var(--mantine-color-body)">
      <Group wrap="nowrap">
        <Image
          src={route.media.assets.thumbnail?.imageUrl?.replace('{size}', '128')}
          alt={route.name}
          w={64}
          h={64}
          fit="cover"
          radius="sm"
        />
        <Stack gap={4} style={{ flex: 1, minWidth: 0 }}>
          <Text size="sm" fw={500} truncate>
            {route.name}
          </Text>
          <Group>
            <Group gap={4}>
              <IconArrowsMaximize size={14} />
              <Text size="xs" c="dimmed">
                {distance(route.distance)}
              </Text>
            </Group>
            <Group gap={4}>
              <IconArrowUp size={14} />
              <Text size="xs" c="dimmed">
                {elevation(route.elevationGain)}
              </Text>
            </Group>
          </Group>
        </Stack>
      </Group>
    </Paper>
  )
}
