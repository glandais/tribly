import { useTranslation } from 'react-i18next'
import { Badge, Button, Group, Paper, Stack, Text, Title } from '@mantine/core'
import {
  IconCalendarTime,
  IconCloudOff,
  IconHistory,
  IconMapPinOff,
  IconRefresh,
} from '@tabler/icons-react'
import type { TripWeatherDto } from '@/api/dto'
import { WeatherStatus } from '@/api/dto'
import { useFormattedDate } from '@/utils/dateFormat'
import { LegWeather } from './LegWeather'
import { WeatherAttribution } from './WeatherAttribution'
import { hasForecast, showsDetailBlock } from './weatherDisplay'

interface StageWeatherSectionProps {
  /** `GET …/trips/{tripSlug}/weather`, read by `useStageDetailData` and prefetched with it. */
  weather: TripWeatherDto | undefined
  /** The stage shown, as `TripStageDto.id` — its leg is picked out of the trip's. */
  stageId: string
  isLoading: boolean
  isFetching: boolean
  onRetry: () => void
  /** Organisers alone hear about a stage without a route (NO_LOCATION): they can fix it. */
  canEdit: boolean
}

/**
 * The weather block of a trip stage's page (docs/LEDGER_*.md API-76): the stage's route as
 * forecast points at their estimated passages, a rain or dry banner, the wind stretch by stretch —
 * the ride's `LegWeather`, without a departure block (the first checkpoint is the start).
 *
 * The state is the stage's own (`leg.status`): a stage already gone (OUT_OF_RANGE) and a status
 * this build does not know draw nothing; one beyond the horizon says when its forecast opens. No
 * data at all — a failed read included — reads as UNAVAILABLE, as on the ride page.
 */
export function StageWeatherSection({
  weather,
  stageId,
  isLoading,
  isFetching,
  onRetry,
  canEdit,
}: StageWeatherSectionProps) {
  const { t } = useTranslation()
  const { formatDate, isGuessedTimezone } = useFormattedDate()

  if (isLoading) return null

  const stage = weather?.stages.find((s) => s.stageId === stageId)
  if (weather && !stage) return null
  const status = stage ? stage.leg.status : WeatherStatus.UNAVAILABLE
  if (!showsDetailBlock(status, canEdit)) return null

  return (
    <Paper withBorder p="lg" component="section" aria-labelledby="stage-weather-title">
      <Stack gap="md">
        <Group justify="space-between" wrap="wrap" gap="xs">
          <Title order={4} id="stage-weather-title">
            {t('rides.weather.title')}
          </Title>
          {status === WeatherStatus.STALE && (
            <Badge
              color="yellow"
              variant="light"
              leftSection={<IconHistory size={12} aria-hidden />}
            >
              {t('rides.weather.stale')}
            </Badge>
          )}
        </Group>

        {status === WeatherStatus.NOT_YET_AVAILABLE && (
          <Group gap="xs" wrap="nowrap">
            <IconCalendarTime size={18} color="var(--mantine-color-dimmed)" aria-hidden />
            <Text size="sm" c="dimmed" suppressHydrationWarning={isGuessedTimezone}>
              {stage?.leg.availableFrom
                ? t('rides.weather.notYetAvailable', { date: formatDate(stage.leg.availableFrom) })
                : t('trips.weather.notYetAvailableNoDate')}
            </Text>
          </Group>
        )}

        {status === WeatherStatus.UNAVAILABLE && (
          <Group gap="sm" wrap="wrap" justify="space-between">
            <Group gap="xs" wrap="nowrap">
              <IconCloudOff size={18} color="var(--mantine-color-dimmed)" aria-hidden />
              <Text size="sm" c="dimmed">
                {t('rides.weather.unavailable')}
              </Text>
            </Group>
            <Button
              variant="light"
              size="xs"
              leftSection={<IconRefresh size={14} aria-hidden />}
              loading={isFetching}
              onClick={onRetry}
            >
              {t('rides.weather.retry')}
            </Button>
          </Group>
        )}

        {status === WeatherStatus.NO_LOCATION && (
          <Group gap="xs" wrap="nowrap">
            <IconMapPinOff size={18} color="var(--mantine-color-dimmed)" aria-hidden />
            <Text size="sm" c="dimmed">
              {t('trips.weather.noLocation')}
            </Text>
          </Group>
        )}

        {stage && hasForecast(status) && <LegWeather leg={stage.leg} subject="stage" />}

        {weather && status !== WeatherStatus.NO_LOCATION && (
          <WeatherAttribution attribution={weather.attribution} fetchedAt={stage?.leg.fetchedAt} />
        )}
      </Stack>
    </Paper>
  )
}
