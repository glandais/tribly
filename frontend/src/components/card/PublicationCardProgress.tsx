import { useTranslation } from 'react-i18next'
import { Group, Progress, Text } from '@mantine/core'
import type { RideDto } from '@/api/dto'

interface PublicationCardProgressProps {
  ride: RideDto
}

export function PublicationCardProgress({ ride }: PublicationCardProgressProps) {
  const { t } = useTranslation()

  // The ride's capacity comes from the server (docs/LEDGER_*.md API-5): a list row carries no
  // `groups`, so summing them here always gave nothing. Null when a group is uncapped — the ride
  // then has no overall limit, and no bar is drawn.
  const totalMax = ride.maxParticipants
  if (totalMax === undefined || totalMax === null || totalMax === 0) {
    return null
  }

  const current = ride.participantCount
  const percentage = Math.min((current / totalMax) * 100, 100)
  const isFull = ride.full

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
