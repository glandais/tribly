import { useTranslation } from 'react-i18next'
import { IconCalendar } from '@tabler/icons-react'
import { Box, Group, SimpleGrid, Skeleton, Stack, Text, Title } from '@mantine/core'
import { Card, CardContent, CardTitle, TypeBadge, Stat } from '../card/common'
import { useGetRouteUsages } from '@/api/endpoints/routes/routes'
import { FormattedDateTime } from '../common/FormattedDate'
import { paths } from '@/config/paths'
import type { RouteUsageDto } from '@/api/dto'

interface RouteUsagesProps {
  teamSlug: string
  routeSlug: string
}

export function RouteUsages({ teamSlug, routeSlug }: RouteUsagesProps) {
  const { t } = useTranslation()

  const { data, isLoading } = useGetRouteUsages(teamSlug, routeSlug, {
    query: { enabled: !!teamSlug && !!routeSlug },
  })

  if (isLoading) {
    return (
      <Box mt="xl">
        <Title order={2} mb="md">
          {t('routes.usages.title')}
        </Title>
        <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
          {[...Array(2)].map((_, i) => (
            <Skeleton key={i} height={96} />
          ))}
        </SimpleGrid>
      </Box>
    )
  }

  const usages = data?.usages ?? []

  // A route with no usages needs no section — keep the page uncluttered.
  if (usages.length === 0) {
    return null
  }

  const usagePath = (usage: RouteUsageDto) =>
    usage.type === 'TRIP'
      ? paths.trip(usage.teamSlug, usage.slug)
      : paths.ride(usage.teamSlug, usage.slug)

  const typeLabel = (usage: RouteUsageDto) =>
    usage.type === 'TRIP' ? t('publicationType.trip') : t('publicationType.ride')

  const viaHint = (usage: RouteUsageDto) => {
    if (usage.viaChildNames.length === 0) {
      return null
    }
    const names = usage.viaChildNames.join(', ')
    return usage.type === 'TRIP'
      ? t('routes.usages.viaTrip', { names })
      : t('routes.usages.viaRide', { names })
  }

  return (
    <Box mt="xl">
      <Title order={2} mb="md">
        {t('routes.usages.title')}
      </Title>
      <SimpleGrid cols={{ base: 1, sm: 2 }} spacing="md">
        {usages.map((usage) => {
          const hint = viaHint(usage)
          return (
            <Card key={`${usage.type}-${usage.slug}`} to={usagePath(usage)}>
              <CardContent>
                <Group align="flex-start" justify="space-between" wrap="nowrap" mb="xs">
                  <CardTitle truncate>{usage.name}</CardTitle>
                  <TypeBadge type={usage.type}>{typeLabel(usage)}</TypeBadge>
                </Group>
                <Stack gap={4} mt="auto">
                  <Stat icon={<IconCalendar size={16} />}>
                    <FormattedDateTime date={usage.dateTime} />
                  </Stat>
                  {hint && (
                    <Text size="sm" c="dimmed">
                      {hint}
                    </Text>
                  )}
                </Stack>
              </CardContent>
            </Card>
          )
        })}
      </SimpleGrid>
    </Box>
  )
}
