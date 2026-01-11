import { useTranslation } from 'react-i18next'
import { IconArrowsMaximize, IconArrowUp } from '@tabler/icons-react'
import { Paper, Group, Text, Image, Stack, Loader } from '@mantine/core'
import { useGetRoute } from '@/api/endpoints/routes/routes'

interface RoutePreviewProps {
  routeSlug: string
  teamSlug: string
}

export function RoutePreview({ routeSlug, teamSlug }: RoutePreviewProps) {
  const { t } = useTranslation()
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
      <Group gap="md" wrap="nowrap">
        <Image
          src={route.media.assets.thumbnail?.url}
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
          <Group gap="md">
            <Group gap={4}>
              <IconArrowsMaximize size={14} />
              <Text size="xs" c="dimmed">
                {t('distance', { distance: (route.distance / 1000).toFixed(1) })}
              </Text>
            </Group>
            <Group gap={4}>
              <IconArrowUp size={14} />
              <Text size="xs" c="dimmed">
                {t('elevation', { elevation: route.elevationGain })}
              </Text>
            </Group>
          </Group>
        </Stack>
      </Group>
    </Paper>
  )
}
