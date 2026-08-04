import { afterEach, describe, expect, it, vi } from 'vitest'
import dayjs from 'dayjs'
import utc from 'dayjs/plugin/utc'
import { getInitialCalendarRange } from './useCalendarDateRange'
import { getVisibleRange } from '@/components/calendar/calendarRange'

dayjs.extend(utc)

/**
 * The contract between the prefetched window and the first grid `CalendarView` reports on mount:
 * the grid must fall *inside* the window, or `handleDateRangeChange` re-keys the query and the
 * SSR-prefetched events are thrown away one render after hydration. With a rolling `now - 1 month`
 * window it held for most of the month and broke for its last days — 40 of 365 — which is exactly
 * the kind of defect a crawl on the 4th cannot see. Hence a check on every day of a year, with the
 * visitor's calendar date offset either side of the UTC one to stand in for a zone east or west of
 * UTC.
 */
describe('getInitialCalendarRange', () => {
  afterEach(() => {
    vi.useRealTimers()
  })

  it('contains the initial month grid every day of a year, in any zone', () => {
    vi.useFakeTimers()
    for (let day = 0; day < 365; day += 1) {
      const now = new Date(Date.UTC(2026, 0, 1, 12, 0))
      now.setUTCDate(now.getUTCDate() + day)
      vi.setSystemTime(now)
      const window = getInitialCalendarRange()

      // `CalendarView` seeds its date in the visitor's zone, which can be the UTC day either side.
      for (const offset of [-1, 0, 1]) {
        const date = dayjs(now).utc().add(offset, 'day').format('YYYY-MM-DD')
        const grid = getVisibleRange(date, 'month')
        const where = `${date} (utc ${now.toISOString()})`

        expect(grid.start.toISOString() >= window.from, `grid starts before window: ${where}`).toBe(
          true
        )
        expect(grid.end.toISOString() <= window.to, `grid ends after window: ${where}`).toBe(true)
      }
    }
  })

  it('only changes at month boundaries, so the query key is stable within a month', () => {
    vi.useFakeTimers()
    vi.setSystemTime(new Date('2026-08-04T09:30:00Z'))
    const early = getInitialCalendarRange()
    vi.setSystemTime(new Date('2026-08-27T23:00:00Z'))
    expect(getInitialCalendarRange()).toEqual(early)
    vi.setSystemTime(new Date('2026-09-01T00:00:00Z'))
    expect(getInitialCalendarRange()).not.toEqual(early)
  })
})
