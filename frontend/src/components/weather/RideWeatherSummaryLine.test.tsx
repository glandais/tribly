import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import type { RideWeatherSummaryDto } from '@/api/dto'
import { WeatherStatus } from '@/api/dto'
import { usePreferencesStore } from '@/store/preferencesStore'
import fr from '@/locales/fr/common.json'

// The real French wording, interpolated: the line is read as a rider reads it.
vi.mock('react-i18next', () => {
  const messages = fr as Record<string, string>
  const t = (key: string, options?: Record<string, unknown>) =>
    (messages[key] ?? key).replace(/\{\{(\w+)\}\}/g, (_, name: string) =>
      String(options?.[name] ?? '')
    )
  return { useTranslation: () => ({ t, i18n: { language: 'fr' } }) }
})

import { RideWeatherSummaryLine } from './RideWeatherSummaryLine'

// Intl and the locale files separate value and unit with a no-break space; compare on plain spaces.
const plain = (s: string | null | undefined) => (s ?? '').replace(/[  ]/g, ' ')

function renderLine(summary: RideWeatherSummaryDto | undefined) {
  return render(
    <MantineProvider>
      <RideWeatherSummaryLine summary={summary} />
    </MantineProvider>
  )
}

const okSummary: RideWeatherSummaryDto = {
  status: WeatherStatus.OK,
  weatherCode: 80,
  condition: 'SHOWERS',
  daylight: true,
  temperature: 9.4,
  temperatureMin: 8.2,
  temperatureMax: 14.4,
  maxPrecipitationProbability: 70,
  wind: { speed: 18.3, gusts: 35, direction: 225, compass: 'SW' },
  rainAlert: { probability: 70, time: '2026-10-08T10:00:00Z', condition: 'SHOWERS' },
}

describe('RideWeatherSummaryLine', () => {
  beforeEach(() => usePreferencesStore.setState({ unitSystem: 'METRIC' }))
  afterEach(cleanup)

  it('reads the range min → max, the wind, and the rain badge', () => {
    renderLine(okSummary)
    const line = screen.getByTestId('ride-weather-summary')
    const text = plain(line.textContent)
    expect(text).toContain('8 → 14 °C')
    expect(text).toContain('SO 18 km/h')
    expect(text).toMatch(/Averses dès \d{1,2}:\d{2}/)
    expect(screen.getByRole('img', { name: 'Averses' })).toBeTruthy()
  })

  it('names the rain badge after what falls, its probability in the tooltip', () => {
    renderLine({ ...okSummary, rainAlert: { ...okSummary.rainAlert!, condition: 'SNOW' } })
    const badge = screen.getByTitle('Probabilité de pluie : 70 %')
    expect(plain(badge.textContent)).toMatch(/^Neige dès \d{1,2}:\d{2}$/)
    cleanup()
    renderLine({ ...okSummary, rainAlert: { ...okSummary.rainAlert!, condition: 'THUNDERSTORM' } })
    expect(plain(screen.getByTestId('ride-weather-summary').textContent)).toMatch(/Orage dès/)
  })

  it('falls back to « Pluie » when the hour does not precipitate, or is unknown here', () => {
    // The alert is raised on probability alone: « Couvert dès 10:00 » would make no sense.
    renderLine({ ...okSummary, rainAlert: { ...okSummary.rainAlert!, condition: 'OVERCAST' } })
    expect(plain(screen.getByTestId('ride-weather-summary').textContent)).toMatch(/Pluie dès/)
    cleanup()
    renderLine({
      ...okSummary,
      rainAlert: {
        ...okSummary.rainAlert!,
        condition: 'VOLCANIC_ASH' as RideWeatherSummaryDto['condition'] & string,
      },
    })
    expect(plain(screen.getByTestId('ride-weather-summary').textContent)).toMatch(/Pluie dès/)
  })

  it('converts to °F and mph for an imperial reader', () => {
    usePreferencesStore.setState({ unitSystem: 'IMPERIAL' })
    renderLine(okSummary)
    const text = plain(screen.getByTestId('ride-weather-summary').textContent)
    expect(text).toContain('47 → 58 °F')
    expect(text).toContain('SO 11 mph')
  })

  it('shows a single temperature when the range rounds to one degree', () => {
    renderLine({ ...okSummary, temperatureMin: 12.2, temperatureMax: 12.4, rainAlert: undefined })
    const text = plain(screen.getByTestId('ride-weather-summary').textContent)
    expect(text).toContain('12 °C')
    expect(text).not.toContain('→')
    expect(text).not.toContain(' dès ')
  })

  it('says when the forecast opens, seven days before', () => {
    renderLine({ status: WeatherStatus.NOT_YET_AVAILABLE, availableFrom: '2026-10-10T12:00:00Z' })
    expect(plain(screen.getByTestId('ride-weather-summary').textContent)).toMatch(
      /^Météo disponible à partir du \d{1,2} octobre 2026$/
    )
  })

  it('flags an old forecast', () => {
    renderLine({ ...okSummary, status: WeatherStatus.STALE })
    expect(screen.getByRole('img', { name: 'Prévision ancienne' })).toBeTruthy()
  })

  it('draws a plain cloud for a condition this build does not know', () => {
    renderLine({ ...okSummary, condition: 'VOLCANIC_ASH' as RideWeatherSummaryDto['condition'] })
    expect(screen.getByRole('img', { name: 'Météo' })).toBeTruthy()
  })

  it('draws nothing without a summary, or for a status it does not know', () => {
    renderLine(undefined)
    expect(screen.queryByTestId('ride-weather-summary')).toBeNull()
    cleanup()
    renderLine({ ...okSummary, status: 'SOMETHING_NEW' as WeatherStatus })
    expect(screen.queryByTestId('ride-weather-summary')).toBeNull()
  })
})
