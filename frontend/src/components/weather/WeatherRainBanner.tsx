import { useTranslation } from 'react-i18next'
import { Alert } from '@mantine/core'
import { IconSun, IconUmbrella } from '@tabler/icons-react'
import type { WeatherRainAlertDto } from '@/api/dto'
import { useUnits } from '@/hooks/useUnits'
import { useRendezvousFormat } from '@/hooks/useRendezvousFormat'
import { useWeatherLabels } from './useWeatherLabels'
import { rainAlertCondition } from './weatherDisplay'

interface WeatherRainBannerProps {
  rainAlert: WeatherRainAlertDto | undefined
  /** The ride's or stage's zone: passages are rendezvous read in it (docs/LEDGER_*.md API-60). */
  timezone?: string
}

/**
 * Rain or dry, for the whole leg: the first point where rain gets likely (50 % or more, the
 * server's threshold), or a word that none is expected.
 */
export function WeatherRainBanner({ rainAlert, timezone }: WeatherRainBannerProps) {
  const { t } = useTranslation()
  const { distance } = useUnits()
  const { formatTime, isGuessedText } = useRendezvousFormat(timezone)
  const labels = useWeatherLabels()

  if (!rainAlert) {
    return (
      <Alert color="teal" variant="light" icon={<IconSun size={18} aria-hidden />} p="xs">
        {t('rides.weather.rain.dry')}
      </Alert>
    )
  }

  const condition = labels.condition(rainAlertCondition(rainAlert.condition))
  const probability = rainAlert.probability
  const time = formatTime(rainAlert.time)
  return (
    <Alert color="blue" variant="light" icon={<IconUmbrella size={18} aria-hidden />} p="xs">
      <span suppressHydrationWarning={isGuessedText}>
        {rainAlert.distance !== undefined
          ? t('rides.weather.rain.alertAt', {
              condition,
              probability,
              time,
              distance: distance(rainAlert.distance),
            })
          : t('rides.weather.rain.alert', { condition, probability, time })}
      </span>
    </Alert>
  )
}
