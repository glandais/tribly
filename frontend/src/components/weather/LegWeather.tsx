import { useTranslation } from 'react-i18next'
import { Stack, Text } from '@mantine/core'
import type { WeatherLegDto } from '@/api/dto'
import { WeatherStatus } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'
import { useFormattedDate } from '@/utils/dateFormat'
import { WeatherCheckpointStrip } from './WeatherCheckpointStrip'
import { WeatherRainBanner } from './WeatherRainBanner'
import { WindExposureBar } from './WindExposureBar'
import { hasForecast } from './weatherDisplay'

/** One group's route, or one trip stage's: when it leaves and arrives, at what speed, then rain, points and wind. */
export function LegWeather({
  leg,
  subject = 'group',
}: {
  leg: WeatherLegDto
  /** What rides the leg — only the wording of a missing route or speed changes. */
  subject?: 'group' | 'stage'
}) {
  const { t } = useTranslation()
  const { distance, speed } = useUnits()
  const { formatTime, isGuessedTimezone } = useFormattedDate()

  if (leg.status === WeatherStatus.NO_LOCATION) {
    return (
      <Text size="sm" c="dimmed">
        {subject === 'stage' ? t('trips.weather.noRoute') : t('rides.weather.leg.noRoute')}
      </Text>
    )
  }
  if (!hasForecast(leg.status) || leg.checkpoints.length === 0) {
    return (
      <Text size="sm" c="dimmed">
        {t('rides.weather.leg.unavailable')}
      </Text>
    )
  }

  return (
    <Stack gap="sm">
      <Stack gap={2}>
        <Text size="sm" fw={600} suppressHydrationWarning={isGuessedTimezone}>
          {t('rides.weather.leg.schedule', {
            start: formatTime(leg.startTime),
            arrival: formatTime(leg.arrivalTime),
            distance: distance(leg.distance),
          })}
        </Text>
        <Text size="xs" c="dimmed">
          {leg.speedIsDefault
            ? t(
                subject === 'stage'
                  ? 'trips.weather.speedDefault'
                  : 'rides.weather.leg.speedDefault',
                {
                  speed: speed(leg.averageSpeed),
                }
              )
            : t('rides.weather.leg.speed', { speed: speed(leg.averageSpeed) })}
        </Text>
      </Stack>
      <WeatherRainBanner rainAlert={leg.rainAlert} />
      <WeatherCheckpointStrip checkpoints={leg.checkpoints} />
      <WindExposureBar
        segments={leg.segments}
        exposure={leg.windExposure}
        distance={leg.distance}
      />
    </Stack>
  )
}
