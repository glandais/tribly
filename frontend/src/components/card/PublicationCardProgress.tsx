import { useTranslation } from 'react-i18next'
import { Group, Progress, Text } from '@mantine/core'
import type { RideDto } from '@/api/dto'

interface PublicationCardProgressProps {
  ride: RideDto
}

export function PublicationCardProgress({ ride }: PublicationCardProgressProps) {
  const { t } = useTranslation()

  // Calculate total capacity from groups
  const groups = ride.groups || []
  const hasCapacity = groups.some(
    (g) => g.maxParticipants !== undefined && g.maxParticipants !== null
  )

  if (!hasCapacity) {
    // No capacity limits defined - don't show progress
    return null
  }

  const totalMax = groups.reduce((sum, g) => sum + (g.maxParticipants ?? 0), 0)
  const current = ride.participantCount

  if (totalMax === 0) {
    return null
  }

  const percentage = Math.min((current / totalMax) * 100, 100)
  // `full` is computed server-side (contract 1.3.0); fall back on the local sum only
  // for a ride whose groups carry no capacity at all.
  const isFull = ride.full ?? totalMax - current <= 0

  // Color based on fill percentage
  const color = isFull ? 'red' : percentage >= 80 ? 'yellow' : 'green'

  return (
    <Group gap="xs" wrap="nowrap">
      <Progress value={percentage} color={color} size="sm" style={{ flex: 1 }} />
      <Text size="xs" c="dimmed" style={{ whiteSpace: 'nowrap' }}>
        {isFull ? t('spotsFull') : t('spotsOf', { current, max: totalMax })}
      </Text>
    </Group>
  )
}
