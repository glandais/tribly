import { useTranslation } from 'react-i18next'
import { Badge, Group, Text } from '@mantine/core'
import { IconCalendarTime, IconHistory, IconUmbrella, IconWind } from '@tabler/icons-react'
import type { RideWeatherSummaryDto } from '@/api/dto'
import { WeatherStatus } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'
import { useFormattedDate } from '@/utils/dateFormat'
import { formatTemperature, temperatureToDisplay } from '@/utils/unitFormat'
import { WeatherIcon } from './WeatherIcon'
import { useWeatherLabels } from './useWeatherLabels'
import { rainAlertCondition, showsSummary, temperatureRange } from './weatherDisplay'

interface RideWeatherSummaryLineProps {
  summary: RideWeatherSummaryDto | undefined
}

/**
 * The weather in one line on a ride's card (`PublicationCard`, `NextRideCard`) — and on a trip's
 * (`PublicationCard`, its next stage) or a stage's (`TripStageCard`): the sky at
 * departure, the temperature range min → max from departure to the last estimated arrival, the
 * wind, and a badge when rain gets likely — named after what falls (rain, showers, snow, storm…),
 * its probability in the tooltip. Before the forecast opens (seven days ahead), the date
 * it will. Anything else — no summary, a status this build does not know — draws nothing.
 *
 * Everything here is the server's (`RideDto.weather`, `TripDto.weather`,
 * docs/plans/2026-10-05-weather.md §4): the
 * card only words it and converts the units.
 */
export function RideWeatherSummaryLine({ summary }: RideWeatherSummaryLineProps) {
  const { t } = useTranslation()
  const { temperature, unitSystem } = useUnits()
  const { formatDate, formatTime, isGuessedTimezone } = useFormattedDate()
  const labels = useWeatherLabels()

  if (!summary || !showsSummary(summary)) return null

  if (summary.status === WeatherStatus.NOT_YET_AVAILABLE) {
    return (
      <Group gap={4} wrap="nowrap" data-testid="ride-weather-summary">
        <IconCalendarTime size={16} color="var(--mantine-color-dimmed)" aria-hidden />
        <Text size="sm" c="dimmed" suppressHydrationWarning={isGuessedTimezone}>
          {t('rides.weather.summary.availableFrom', { date: formatDate(summary.availableFrom) })}
        </Text>
      </Group>
    )
  }

  const range = temperatureRange(summary)
  let rangeText: string | undefined
  if (range) {
    rangeText =
      temperatureToDisplay(range.min, unitSystem) === temperatureToDisplay(range.max, unitSystem)
        ? temperature(range.max)
        : t('rides.weather.summary.range', {
            min: formatTemperature(range.min, unitSystem),
            max: temperature(range.max),
          })
  }

  return (
    <Group
      gap="xs"
      wrap="wrap"
      role="group"
      aria-label={t('rides.weather.title')}
      data-testid="ride-weather-summary"
    >
      {(summary.condition || rangeText) && (
        <Group gap={4} wrap="nowrap">
          <WeatherIcon
            condition={summary.condition}
            daylight={summary.daylight ?? true}
            size={16}
          />
          {rangeText && (
            <Text size="sm" c="dimmed">
              {rangeText}
            </Text>
          )}
        </Group>
      )}
      {summary.wind && (
        <Group gap={4} wrap="nowrap">
          <IconWind size={16} color="var(--mantine-color-dimmed)" aria-hidden />
          <Text size="sm" c="dimmed">
            {labels.wind(summary.wind)}
          </Text>
        </Group>
      )}
      {summary.rainAlert && (
        <Badge
          size="sm"
          color="blue"
          variant="light"
          leftSection={<IconUmbrella size={12} aria-hidden />}
          title={t('rides.weather.rain.probability', {
            probability: summary.rainAlert.probability,
          })}
        >
          <span suppressHydrationWarning={isGuessedTimezone}>
            {t('rides.weather.summary.rainAlert', {
              condition: labels.condition(rainAlertCondition(summary.rainAlert.condition)),
              time: formatTime(summary.rainAlert.time),
            })}
          </span>
        </Badge>
      )}
      {summary.status === WeatherStatus.STALE && (
        <IconHistory
          size={14}
          color="var(--mantine-color-dimmed)"
          role="img"
          aria-label={t('rides.weather.stale')}
          title={t('rides.weather.stale')}
        />
      )}
    </Group>
  )
}
