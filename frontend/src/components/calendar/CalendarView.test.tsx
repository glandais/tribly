import { describe, it, expect, vi, beforeAll, afterAll } from 'vitest'
import { renderToString } from 'react-dom/server'
import { MemoryRouter } from 'react-router-dom'
import { MantineProvider } from '@mantine/core'

vi.mock('react-i18next', () => ({
  useTranslation: () => ({ t: (key: string) => key, i18n: { language: 'en' } }),
}))

import { CalendarView } from './CalendarView'

/**
 * Between midnight and the visitor's offset, the date in their zone is not the date in UTC — the
 * zone the SSR container runs in, and the one `useEffectiveTimezone` answers during the server and
 * hydration renders. Anything the server render derives from a bare `dayjs()` then reads the
 * *process* zone instead, and the browser renders another day: a text mismatch, React error #418.
 *
 * Here the process plays the browser (Europe/Paris, already on the 28th) while `renderToString`
 * takes the server snapshot (UTC, still the 27th): whatever the markup shows must be the 27th.
 */
describe('CalendarView server render', () => {
  const previousTz = process.env.TZ

  beforeAll(() => {
    process.env.TZ = 'Europe/Paris'
    vi.useFakeTimers({ toFake: ['Date'] })
    vi.setSystemTime(new Date('2026-09-27T22:30:00Z'))
  })

  afterAll(() => {
    vi.useRealTimers()
    process.env.TZ = previousTz
  })

  function render(): string {
    return renderToString(
      <MantineProvider>
        <MemoryRouter>
          <CalendarView events={[]} isLoading={false} onDateRangeChange={() => {}} />
        </MemoryRouter>
      </MantineProvider>
    )
  }

  it('titles the mobile agenda with the day in the render timezone, not the process one', () => {
    const heading = /mobileMonthViewEventsHeader[^>]*>([^<]*)</.exec(render())?.[1]
    expect(heading).toBe('Sunday, September 27')
  })

  it('leaves "today" unmarked until mounted, since React never patches the attribute', () => {
    expect(render()).not.toContain('data-today')
  })
})
