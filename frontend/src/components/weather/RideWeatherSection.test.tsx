import { afterEach, describe, expect, it, vi } from 'vitest'
import { cleanup, fireEvent, render, screen } from '@testing-library/react'
import { MantineProvider } from '@mantine/core'
import type { RideDto, RideWeatherDto, WeatherLegDto } from '@/api/dto'
import { Status, WeatherStatus } from '@/api/dto'
import fr from '@/locales/fr/common.json'

vi.mock('react-i18next', () => {
  const messages = fr as Record<string, string>
  const t = (key: string, options?: Record<string, unknown>) =>
    (messages[key] ?? key).replace(/\{\{(\w+)\}\}/g, (_, name: string) =>
      String(options?.[name] ?? '')
    )
  return { useTranslation: () => ({ t, i18n: { language: 'fr' } }) }
})

import { RideWeatherSection } from './RideWeatherSection'

// ScrollArea and SegmentedControl construct a ResizeObserver; the shared setup's mock is an arrow
// function, which `new` refuses.
class NoopResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}
vi.stubGlobal('ResizeObserver', NoopResizeObserver)

const plain = (s: string | null | undefined) => (s ?? '').replace(/[  ]/g, ' ')

const ride = (overrides: Partial<RideDto> = {}): RideDto =>
  ({
    status: Status.PUBLISHED,
    finished: false,
    registeredGroupId: 'g2',
    groups: [
      { id: 'g1', name: 'Rapide' },
      { id: 'g2', name: 'Cool' },
    ],
    ...overrides,
  }) as RideDto

const conditions = {
  time: '2026-10-08T07:00:00Z',
  weatherCode: 0,
  condition: 'CLEAR' as const,
  daylight: true,
  temperature: 12,
  apparentTemperature: 10,
  precipitationProbability: 10,
  precipitation: 0,
  wind: { speed: 15, direction: 225, compass: 'SW' as const },
}

const leg = (groupId: string, overrides: Partial<WeatherLegDto> = {}): WeatherLegDto => ({
  groupId,
  status: WeatherStatus.OK,
  startTime: '2026-10-08T07:00:00Z',
  averageSpeed: 25,
  speedIsDefault: true,
  distance: 50_000,
  arrivalTime: '2026-10-08T09:00:00Z',
  checkpoints: [
    {
      index: 0,
      kind: 'START',
      distance: 0,
      time: '2026-10-08T07:00:00Z',
      weather: conditions,
      relativeWind: 'HEAD',
      headwind: 12,
      relativeWindAngle: 180,
    },
    {
      index: 1,
      kind: 'FINISH',
      distance: 50_000,
      time: '2026-10-08T09:00:00Z',
      weather: conditions,
      relativeWind: 'TAIL',
      headwind: -10,
      relativeWindAngle: 0,
    },
  ],
  segments: [{ fromDistance: 0, toDistance: 50_000, relativeWind: 'HEAD', headwind: 12 }],
  windExposure: { head: 50_000, cross: 0, tail: 0 },
  ...overrides,
})

const okWeather: RideWeatherDto = {
  status: WeatherStatus.OK,
  fetchedAt: '2026-10-07T18:00:00Z',
  departure: { status: WeatherStatus.OK, conditions },
  legs: [leg('g1', { speedIsDefault: false, averageSpeed: 30 }), leg('g2')],
  attribution: { name: 'Open-Meteo.com', url: 'https://open-meteo.com/' },
}

function renderSection(props: {
  ride?: RideDto
  weather?: RideWeatherDto
  isError?: boolean
  canEdit?: boolean
  onRetry?: () => void
}) {
  return render(
    <MantineProvider>
      <RideWeatherSection
        ride={props.ride ?? ride()}
        weather={props.weather}
        isLoading={false}
        isError={props.isError ?? false}
        isFetching={false}
        onRetry={props.onRetry ?? (() => {})}
        canEdit={props.canEdit ?? false}
      />
    </MantineProvider>
  )
}

describe('RideWeatherSection', () => {
  afterEach(cleanup)

  it("opens on the reader's group, says the default speed, and turns the arrows", () => {
    renderSection({ weather: okWeather })
    const text = plain(document.body.textContent)
    expect(text).toContain('vitesse par défaut')
    expect(text).toContain('Open-Meteo.com')
    const headArrow = screen.getByRole('img', { name: 'Vent de face, 15 km/h' })
    expect(headArrow.style.transform).toBe('rotate(180deg)')
    expect(screen.getByRole('img', { name: 'Vent dans le dos, 15 km/h' }).style.transform).toBe(
      'rotate(0deg)'
    )
  })

  it('switches group', () => {
    renderSection({ weather: okWeather })
    fireEvent.click(screen.getByText('Rapide'))
    expect(plain(document.body.textContent)).not.toContain('vitesse par défaut')
  })

  it('offers to retry when the forecast is unavailable, or the read failed', () => {
    const onRetry = vi.fn()
    renderSection({ isError: true, onRetry })
    fireEvent.click(screen.getByRole('button', { name: 'Réessayer' }))
    expect(onRetry).toHaveBeenCalled()
  })

  it('speaks of a missing place to organisers only', () => {
    const noLocation = { ...okWeather, status: WeatherStatus.NO_LOCATION, legs: [] }
    renderSection({ weather: noLocation, canEdit: false })
    expect(screen.queryByText(/Ajoutez un lieu/)).toBeNull()
    cleanup()
    renderSection({ weather: noLocation, canEdit: true })
    expect(screen.getByText(/Ajoutez un lieu/)).toBeTruthy()
  })

  it('draws nothing for a finished or cancelled ride, or an unknown status', () => {
    const { container: finished } = renderSection({
      ride: ride({ finished: true }),
      weather: okWeather,
    })
    expect(finished.querySelector('section')).toBeNull()
    cleanup()
    const { container: cancelled } = renderSection({
      ride: ride({ status: Status.CANCELLED }),
      weather: okWeather,
    })
    expect(cancelled.querySelector('section')).toBeNull()
    cleanup()
    const { container: unknown } = renderSection({
      weather: { ...okWeather, status: 'SOMETHING_NEW' as WeatherStatus },
    })
    expect(unknown.querySelector('section')).toBeNull()
  })
})
