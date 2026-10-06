import { useTranslation } from 'react-i18next'
import { Anchor, Group, Text } from '@mantine/core'
import type { Instant, WeatherAttributionDto } from '@/api/dto'
import { useFormattedDate } from '@/utils/dateFormat'

interface WeatherAttributionProps {
  attribution: WeatherAttributionDto
  fetchedAt: Instant | undefined
}

/** The credit the forecast's licence asks for, and when it was fetched — a weather block's foot. */
export function WeatherAttribution({ attribution, fetchedAt }: WeatherAttributionProps) {
  const { t } = useTranslation()
  const { formatDate, formatTime, isToday, isGuessedTimezone } = useFormattedDate()

  return (
    <Group justify="space-between" wrap="wrap" gap="xs">
      <Text size="xs" c="dimmed">
        {t('rides.weather.attribution')}{' '}
        <Anchor href={attribution.url} target="_blank" rel="noopener noreferrer" size="xs">
          {attribution.name}
        </Anchor>
      </Text>
      {fetchedAt && (
        <Text size="xs" c="dimmed" suppressHydrationWarning={isGuessedTimezone}>
          {isToday(fetchedAt)
            ? t('rides.weather.updatedAt', { time: formatTime(fetchedAt) })
            : t('rides.weather.updatedOn', {
                date: formatDate(fetchedAt),
                time: formatTime(fetchedAt),
              })}
        </Text>
      )}
    </Group>
  )
}
